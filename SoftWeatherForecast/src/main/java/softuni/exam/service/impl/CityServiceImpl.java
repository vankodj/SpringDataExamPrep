package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.ImportCityDTO;
import softuni.exam.models.entity.City;
import softuni.exam.repository.CityRepository;
import softuni.exam.service.CityService;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

@Service
public class CityServiceImpl implements CityService {
    private final CityRepository cityRepository;
    private final Gson gson;
    private final ModelMapper modelMapper;
    private final Validator validator;

    @Autowired
    public CityServiceImpl(CityRepository cityRepository, Gson gson, ModelMapper modelMapper) {
        this.cityRepository = cityRepository;
        this.gson = gson;
        this.modelMapper = modelMapper;
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Override
    public boolean areImported() {
        return this.cityRepository.count() > 0;
    }

    @Override
    public String readCitiesFileContent() throws IOException {
        Path path = Path.of("src/main/resources/files/json/cities.json");
        return Files.readString(path);
    }

    @Override
    public String importCities() throws IOException {
       StringBuilder sb = new StringBuilder();
       String json = this.readCitiesFileContent();
        ImportCityDTO[] cities = this.gson.fromJson(json,ImportCityDTO[].class);
        for (ImportCityDTO city : cities) {
            Set<ConstraintViolation<ImportCityDTO>> validate
                    = this.validator.validate(city);
            if (validate.isEmpty()){
              if (this.cityRepository.findByCityName(city.getCityName()).isEmpty()){
                  City cityToAdd = this.modelMapper.map(city,City.class);
                  this.cityRepository.save(cityToAdd);
                  sb.append(String.format("Successfully imported city %s- %d"
                  ,cityToAdd.getCityName(),cityToAdd.getPopulation()))
                          .append(System.lineSeparator());
              }else{
                  sb.append("Invalid city").append(System.lineSeparator());
              }
            }else{
                sb.append("Invalid city").append(System.lineSeparator());
            }
        }

        return sb.toString();
    }

    @Override
    public City findCityById(Long id) {
        return this.cityRepository.findCityById(id).orElse(null);
    }
}
