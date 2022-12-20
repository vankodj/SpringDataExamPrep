package softuni.exam.service;


import softuni.exam.models.entity.Car;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.Optional;

// TODO: Implement all methods
public interface CarService {

    boolean areImported();

    String readCarsFromFile() throws IOException;

    String importCars() throws IOException, JAXBException;

    Car findCarById(Long id);
}
