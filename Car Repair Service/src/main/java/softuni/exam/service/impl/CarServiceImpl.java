package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.ImportCarDTO;
import softuni.exam.models.dto.ImportCarRootDTO;
import softuni.exam.models.entity.Car;
import softuni.exam.repository.CarRepository;
import softuni.exam.service.CarService;

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
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final ModelMapper modelMapper;
    private final StringBuilder sb;
    private final Validator validator;
    private final Unmarshaller unmarshaller;
    Path path = Path.of("src/main/resources/files/xml/cars.xml");

    @Autowired
    public CarServiceImpl(CarRepository carRepository, ModelMapper modelMapper, StringBuilder sb) throws JAXBException {
        this.carRepository = carRepository;
        this.modelMapper = modelMapper;
        this.sb = sb;
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
        JAXBContext context = JAXBContext.newInstance(ImportCarRootDTO.class);
        this.unmarshaller = context.createUnmarshaller();
    }

    @Override
    public boolean areImported() {
        return this.carRepository.count() > 0;
    }

    @Override
    public String readCarsFromFile() throws IOException {
        return Files.readString(path);
    }

    @Override
    public String importCars() throws IOException, JAXBException {
        ImportCarRootDTO cars = (ImportCarRootDTO)
                this.unmarshaller.unmarshal(new FileReader(path.toAbsolutePath().toString()));

        return cars.getCars().stream()
                .map(this::importCar).collect(Collectors.joining("\n"));
    }

    @Override
    public Car findCarById(Long id) {
        return this.carRepository.findCarById(id).orElse(null);
    }

    private String importCar(ImportCarDTO car) {
        Set<ConstraintViolation<ImportCarDTO>> validate = this.validator.validate(car);
        if (validate.isEmpty()) {
            if (this.carRepository.findByPlateNumber(car.getPlateNumber()).isEmpty()) {
                Car carToAdd = this.modelMapper.map(car, Car.class);
                this.carRepository.save(carToAdd);
                sb.append(String.format("Successfully imported car %s %s with %s number%n"
                        , carToAdd.getCarMake(), carToAdd.getCarModel(), carToAdd.getPlateNumber()));
            } else {
                sb.append("Invalid car").append(System.lineSeparator());
            }
        } else {
            sb.append("Invalid car").append(System.lineSeparator());
        }
        return sb.toString();
    }
}
