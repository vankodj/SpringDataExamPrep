package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.dtos.ImportCarDTO;
import softuni.exam.models.entities.Car;
import softuni.exam.repository.CarRepository;
import softuni.exam.service.CarService;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

@Service
public class CarServiceImpl implements CarService {
   private final CarRepository carRepository;
   private final ModelMapper modelMapper;
   private final Gson gson;
   private final StringBuilder sb;
   private final Validator validator;
   Path path = Path.of("src/main/resources/files/json/cars.json");

   @Autowired
    public CarServiceImpl(CarRepository carRepository, ModelMapper modelMapper, Gson gson) {
        this.carRepository = carRepository;
        this.modelMapper = modelMapper;
        this.gson = gson;
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
        this.sb = new StringBuilder();
    }

    @Override
    public boolean areImported() {
        return this.carRepository.count() >0;
    }

    @Override
    public String readCarsFileContent() throws IOException {
        return Files.readString(path);
    }

    @Override
    public String importCars() throws IOException {
       String json = this.readCarsFileContent();
        ImportCarDTO[] cars = this.gson.fromJson(json,ImportCarDTO[].class);
        for (ImportCarDTO car : cars) {
            Set<ConstraintViolation<ImportCarDTO> > validate = this.validator.validate(car);
            if (validate.isEmpty()){
                if (this.carRepository.findByMake(car.getMake()).isEmpty()){
                    Car carToAdd = this.modelMapper.map(car,Car.class);
                    this.carRepository.save(carToAdd);
                    sb.append(String.format("Successfully imported car - %s - %s"
                            ,carToAdd.getMake(),carToAdd.getModel()))
                            .append(System.lineSeparator());
                }else{
                    sb.append("Invalid car").append(System.lineSeparator());
                }
            }else{
                sb.append("Invalid car").append(System.lineSeparator());
            }
        }

        return sb.toString();
    }

    @Override
    public String getCarsOrderByPicturesCountThenByMake() {
        return null;
    }

    @Override
    public Car findCarById(Long id) {
        return this.carRepository.findCarById(id).orElse(null);
    }
}
