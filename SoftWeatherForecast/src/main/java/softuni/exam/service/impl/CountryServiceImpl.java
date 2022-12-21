package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.ImportCountryDTO;
import softuni.exam.models.entity.Country;
import softuni.exam.repository.CountryRepository;
import softuni.exam.service.CountryService;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

@Service
public class CountryServiceImpl implements CountryService {
    private final CountryRepository countryRepository;
    private final ModelMapper modelMapper;
    private final Gson gson;
    private final Validator validator;

    @Autowired
    public CountryServiceImpl(CountryRepository countryRepository, ModelMapper modelMapper, Gson gson) {
        this.countryRepository = countryRepository;
        this.modelMapper = modelMapper;
        this.gson = gson;
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Override
    public boolean areImported() {
        return this.countryRepository.count() >0;
    }

    @Override
    public String readCountriesFromFile() throws IOException {
        Path path = Path.of("src/main/resources/files/json/countries.json");
        return Files.readString(path);
    }

    @Override
    public String importCountries() throws IOException {
       StringBuilder sb = new StringBuilder();
       String json = this.readCountriesFromFile();
        ImportCountryDTO[] countries = this.gson.fromJson(json,ImportCountryDTO[].class);
        for (ImportCountryDTO country : countries) {
            Set<ConstraintViolation<ImportCountryDTO>> validate
                    = this.validator.validate(country);
            if (validate.isEmpty()){
                if (this.countryRepository.findByCountryName(country.getCountryName()).isEmpty()){
                    Country countryToAdd = this.modelMapper.map(country,Country.class);
                    this.countryRepository.save(countryToAdd);
                    sb.append(String.format("Successfully imported country %s - %s"
                    ,countryToAdd.getCountryName(),countryToAdd.getCurrency()))
                            .append(System.lineSeparator());
                }else{
                    sb.append("Invalid country").append(System.lineSeparator());
                }
            }else{
                sb.append("Invalid country").append(System.lineSeparator());
            }
        }

        return sb.toString();
    }
}
