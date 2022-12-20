package softuni.exam.service;

import softuni.exam.models.entity.Mechanic;

import javax.xml.bind.JAXBException;
import java.io.IOException;

// TODO: Implement all methods
public interface TaskService {

    boolean areImported();

    String readTasksFileContent() throws IOException;

    String importTasks() throws IOException, JAXBException;

    String getCoupeCarTasksOrderByPrice();


}
