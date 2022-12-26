package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.ImportTownDTO;
import softuni.exam.models.entity.Town;
import softuni.exam.repository.TownRepository;
import softuni.exam.service.TownService;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

@Service
public class TownServiceImpl implements TownService {
    private final TownRepository townRepository;
    private final Gson gson;
    private final ModelMapper modelMapper;
    private final Validator validator;
    private final StringBuilder sb;
    private final Path path = Path.of("src/main/resources/files/json/towns.json");


    @Autowired
    public TownServiceImpl(TownRepository townRepository, Gson gson, ModelMapper modelMapper) {
        this.townRepository = townRepository;
        this.gson = gson;
        this.modelMapper = modelMapper;
        this.sb = new StringBuilder();
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Override
    public boolean areImported() {
        return this.townRepository.count() >0;
    }

    @Override
    public String readTownsFileContent() throws IOException {
        return Files.readString(path);
    }

    @Override
    public String importTowns() throws IOException {
       String json = this.readTownsFileContent();
        ImportTownDTO[] towns = this.gson.fromJson(json,ImportTownDTO[].class);
        for (ImportTownDTO town : towns) {
            Set<ConstraintViolation<ImportTownDTO>> validate
                    = this.validator.validate(town);
            if (validate.isEmpty()){
                if (this.townRepository.findByTownName(town.getTownName()).isEmpty()){
                    Town townToAdd = this.modelMapper.map(town,Town.class);
                    this.townRepository.save(townToAdd);
                    sb.append(String.format("Successfully imported town %s - %d"
                    ,townToAdd.getTownName(),townToAdd.getPopulation()))
                            .append(System.lineSeparator());
                }else{
                    sb.append("Invalid town").append(System.lineSeparator());
                }
            }else{
                sb.append("Invalid town").append(System.lineSeparator());
            }
        }

        return sb.toString();
    }

    @Override
    public Town findByTownName(String name) {
        return this.townRepository.findByTownName(name).orElse(null);
    }
}
