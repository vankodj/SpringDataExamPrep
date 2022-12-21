package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.ImportForecastDTO;
import softuni.exam.models.dto.ImportForecastRootDTO;
import softuni.exam.models.entity.City;
import softuni.exam.models.entity.DayOfWeek;
import softuni.exam.models.entity.Forecast;
import softuni.exam.repository.ForecastRepository;
import softuni.exam.service.CityService;
import softuni.exam.service.ForecastService;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ForecastServiceImpl implements ForecastService {
    private final ForecastRepository forecastRepository;
    private final CityService cityService;
    private final Gson gson;
    private final ModelMapper modelMapper;
    private final Validator validator;
    private final Path path = Path.of("src/main/resources/files/xml/forecasts.xml");
    private final Unmarshaller unmarshaller;


    @Autowired
    public ForecastServiceImpl(ForecastRepository forecastRepository, CityService cityService, Gson gson, ModelMapper modelMapper) throws JAXBException {
        this.forecastRepository = forecastRepository;
        this.cityService = cityService;
        this.gson = gson;
        this.modelMapper = modelMapper;
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
        JAXBContext context = JAXBContext.newInstance(ImportForecastRootDTO.class);
        this.unmarshaller = context.createUnmarshaller();
    }

    @Override
    public boolean areImported() {
        return this.forecastRepository.count() >0;
    }

    @Override
    public String readForecastsFromFile() throws IOException {
        return Files.readString(path);
    }

    @Override
    public String importForecasts() throws IOException, JAXBException {
        ImportForecastRootDTO forecasts = (ImportForecastRootDTO)
                this.unmarshaller.unmarshal(new FileReader(path.toAbsolutePath().toString()));
        return forecasts.getForecasts().stream()
                .map(this::importForecast)
                .collect(Collectors.joining("\n"));
    }

    private String importForecast(ImportForecastDTO dto) {
       StringBuilder sb = new StringBuilder();
        Set<ConstraintViolation<ImportForecastDTO>> validate = this.validator.validate(dto);
        if (validate.isEmpty()){
            City city = this.cityService.findCityById(dto.getCity());
         if (this.forecastRepository.findAllByCityAndDayOfWeek(city,dto.getDayOfWeek()).isEmpty()){
             Forecast forecastToAdd = this.modelMapper.map(dto,Forecast.class);
             forecastToAdd.setCity(city);
             this.forecastRepository.save(forecastToAdd);
             sb.append(String.format("Successfully import forecast %s - %.2f"
             ,forecastToAdd.getDayOfWeek(),forecastToAdd.getMaxTemperature()));
         }else{
             sb.append("Invalid forecast");
         }
        }else{
            sb.append("Invalid forecast");
        }
return sb.toString();
    }

    @Override
    public String exportForecasts() {
        StringBuilder sb = new StringBuilder();
        List<Forecast> forecasts = this.forecastRepository.findAllByDayOfWeekAndCity_PopulationLessThanOrderByMaxTemperatureDescIdAsc(DayOfWeek.SUNDAY,150000);
        for (Forecast forecast : forecasts) {
            sb.append(String.format("City: %s:%n" +
                    "\t-min temperature: %.2f%n" +
                    "\t--max temperature: %.2f%n" +
                    "\t---sunrise: %s%n" +
                    "\t----sunset: %s%n",forecast.getCity().getCityName(),forecast.getMinTemperature()
                    ,forecast.getMaxTemperature(),forecast.getSunrise(),forecast.getSunset()));
        }
        return sb.toString();
    }
}
