package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.ImportTaskRootDTO;
import softuni.exam.models.dto.ImportTasksDTO;
import softuni.exam.models.entity.*;
import softuni.exam.repository.MechanicRepository;
import softuni.exam.repository.TaskRepository;
import softuni.exam.service.CarService;
import softuni.exam.service.MechanicService;
import softuni.exam.service.PartService;
import softuni.exam.service.TaskService;

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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final MechanicService mechanicService;

    private final CarService carService;
    private final PartService partService;
    private final ModelMapper modelMapper;
    private final StringBuilder sb;
    private final Validator validator;
    private final Unmarshaller unmarshaller;
    Path path = Path.of("src/main/resources/files/xml/tasks.xml");

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository, MechanicService mechanicService, CarService carService, PartService partService, ModelMapper modelMapper, StringBuilder sb) throws JAXBException {
        this.taskRepository = taskRepository;
        this.mechanicService = mechanicService;
        this.carService = carService;
        this.partService = partService;
        this.modelMapper = modelMapper;
        this.sb = sb;
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
        JAXBContext context = JAXBContext.newInstance(ImportTaskRootDTO.class);
        this.unmarshaller = context.createUnmarshaller();
    }

    @Override
    public boolean areImported() {
        return this.taskRepository.count() >0;
    }

    @Override
    public String readTasksFileContent() throws IOException {
        return Files.readString(path);
    }

    @Override
    public String importTasks() throws IOException, JAXBException {
        ImportTaskRootDTO tasks = (ImportTaskRootDTO)
                this.unmarshaller.unmarshal(new FileReader(path.toAbsolutePath().toString()));

        return tasks.getTasks().stream()
                .map(this::importTask).collect(Collectors.joining("\n"));
    }

    private String importTask(ImportTasksDTO task) {
        Set<ConstraintViolation<ImportTasksDTO>> validate = this.validator.validate(task);
        if (validate.isEmpty()){
            Mechanic mechanic = this.mechanicService.findMechanicByFirstName(task.getMechanic()
                    .getFirstName());
            if (mechanic != null){
                Task taskToAdd = this.modelMapper.map(task,Task.class);
                Car car = this.carService.findCarById(task.getCar().getId());
                Part part = this.partService.findPartById(task.getPart().getId());
                taskToAdd.setMechanic(mechanic);
                taskToAdd.setCar(car);
                taskToAdd.setPart(part);
                this.taskRepository.save(taskToAdd);
                sb.append(String.format("Successfully imported task %.2f"
                ,taskToAdd.getPrice())).append(System.lineSeparator());
            }else{
                sb.append("Invalid task").append(System.lineSeparator());
            }
        }else{
            sb.append("Invalid task").append(System.lineSeparator());
        }


        return sb.toString();
    }

    @Override
    public String getCoupeCarTasksOrderByPrice() {
        List<Task> tasks = this.taskRepository.findAllByCar_CarTypeOrderByPriceDesc(CarType.coupe);
        for (Task task : tasks) {
           sb.append(String.format("Car %s %s with %d km%n" +
                   "\t-Mechanic: %s %s - task №%d%n" +
                   "\t--Engine: %.2f%n" +
                   "\t---Price: %.2f$%n"
           ,task.getCar().getCarMake(),task.getCar().getCarModel(),task.getCar().getKilometers()
           ,task.getMechanic().getFirstName(),task.getMechanic().getLastName()
           ,task.getId(),task.getCar().getEngine(),task.getPrice()));
        }

        return sb.toString();
    }
}
