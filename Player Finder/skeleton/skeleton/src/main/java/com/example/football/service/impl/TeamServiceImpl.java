package com.example.football.service.impl;

import com.example.football.models.dto.ImportTeamDTO;
import com.example.football.models.entity.Team;
import com.example.football.models.entity.Town;
import com.example.football.repository.TeamRepository;
import com.example.football.service.TeamService;
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
public class TeamServiceImpl implements TeamService {
    private final TeamRepository teamRepository;
    private final TownService townService;
    private final Gson gson;
    private final ModelMapper modelMapper;
    private final StringBuilder sb;
    private final Validator validator;
    Path path = Path.of("src/main/resources/files/json/teams.json");


    @Autowired
    public TeamServiceImpl(TeamRepository teamRepository, TownService townService, Gson gson, ModelMapper modelMapper) {
        this.teamRepository = teamRepository;
        this.townService = townService;
        this.gson = gson;
        this.modelMapper = modelMapper;
        this.sb = new StringBuilder();
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();

    }


    @Override
    public boolean areImported() {
        return this.teamRepository.count() >0;
    }

    @Override
    public String readTeamsFileContent() throws IOException {
        return Files.readString(path);
    }

    @Override
    public String importTeams() throws IOException {
        String json = this.readTeamsFileContent();
        ImportTeamDTO[] teams = this.gson.fromJson(json,ImportTeamDTO[].class);
        for (ImportTeamDTO team : teams) {
            Set<ConstraintViolation<ImportTeamDTO>> validate
                    = this.validator.validate(team);
            if (validate.isEmpty()){
               if (this.teamRepository.findByName(team.getName()).isEmpty()){
                   Town town = this.townService.findByName(team.getTownName());
                   Team teamToAdd = this.modelMapper.map(team,Team.class);
                   teamToAdd.setTown(town);
                   this.teamRepository.save(teamToAdd);
                   sb.append(String.format("Successfully imported Team %s - %d"
                   ,teamToAdd.getName(),teamToAdd.getFanBase())).append(System.lineSeparator());
               }else{
                   sb.append("Invalid team").append(System.lineSeparator());
               }
            }else{
                sb.append("Invalid team").append(System.lineSeparator());
            }
        }

        return sb.toString();
    }

    @Override
    public Team findByName(String name) {
        return this.teamRepository.findByName(name).orElse(null);
    }
}
