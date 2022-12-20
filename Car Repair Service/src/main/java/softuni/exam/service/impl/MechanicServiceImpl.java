package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.ImportMechanicDTO;
import softuni.exam.models.entity.Mechanic;
import softuni.exam.repository.MechanicRepository;
import softuni.exam.service.MechanicService;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

@Service
public class MechanicServiceImpl implements MechanicService {
    private final MechanicRepository mechanicRepository;
    private final Gson gson;
    private final ModelMapper modelMapper;
    private final Validator validator;
    private final Path path = Path.of("src/main/resources/files/json/mechanics.json");
    private final StringBuilder sb;

    @Autowired
    public MechanicServiceImpl(MechanicRepository mechanicRepository, Gson gson, ModelMapper modelMapper, StringBuilder sb) {
        this.mechanicRepository = mechanicRepository;
        this.gson = gson;
        this.modelMapper = modelMapper;
        this.sb = sb;
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();

    }

    @Override
    public boolean areImported() {
        return this.mechanicRepository.count() >0;
    }

    @Override
    public String readMechanicsFromFile() throws IOException {
        return Files.readString(path);
    }

    @Override
    public String importMechanics() throws IOException {
        String json = this.readMechanicsFromFile();
        ImportMechanicDTO[] mechanics = this.gson.fromJson(json,ImportMechanicDTO[].class);
        for (ImportMechanicDTO mechanic : mechanics) {
            Set<ConstraintViolation<ImportMechanicDTO>> validate
                    = this.validator.validate(mechanic);
            if (validate.isEmpty()){
                if (this.mechanicRepository.findByEmail(mechanic.getEmail()).isEmpty()){
                    Mechanic mechanicToAdd = this.modelMapper.map(mechanic,Mechanic.class);
                    this.mechanicRepository.save(mechanicToAdd);
                    sb.append(String.format("Successfully imported mechanic %s %s"
                    ,mechanicToAdd.getFirstName(),mechanicToAdd.getLastName()))
                            .append(System.lineSeparator());
                }else{
                    sb.append("Invalid mechanic").append(System.lineSeparator());
                }
            }else{
                sb.append("Invalid mechanic").append(System.lineSeparator());
            }
        }
        return sb.toString();
    }

    @Override
    public Mechanic findMechanicByFirstName(String name) {
        return this.mechanicRepository.findMechanicByFirstName(name).orElse(null);
    }
}
