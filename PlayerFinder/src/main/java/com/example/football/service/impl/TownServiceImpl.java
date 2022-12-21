package com.example.football.service.impl;

import com.example.football.models.dto.ImportTownDTO;
import com.example.football.models.entity.Town;
import com.example.football.repository.TownRepository;
import com.example.football.service.TownService;
import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private final StringBuilder sb;
    private final Validator validator;
    Path path = Path.of("src/main/resources/files/json/towns.json");

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
                if (this.townRepository.findByName(town.getName()).isEmpty()){
                    Town townToAdd = this.modelMapper.map(town,Town.class);
                    this.townRepository.save(townToAdd);
                    sb.append(String.format("Successfully imported Town %s - %d"
                    ,townToAdd.getName(),townToAdd.getPopulation())).append(System.lineSeparator());
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
    public Town findByName(String name) {
        return this.townRepository.findByName(name).orElse(null);
    }
}
