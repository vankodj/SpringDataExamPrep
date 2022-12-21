package com.example.football.service.impl;

import com.example.football.models.dto.ImportPlayerDTO;
import com.example.football.models.dto.ImportPlayerRootDTO;
import com.example.football.models.entity.Player;
import com.example.football.models.entity.Stat;
import com.example.football.models.entity.Team;
import com.example.football.models.entity.Town;
import com.example.football.repository.PlayerRepository;
import com.example.football.service.PlayerService;
import com.example.football.service.StatService;
import com.example.football.service.TeamService;
import com.example.football.service.TownService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PlayerServiceImpl implements PlayerService {
    private final PlayerRepository playerRepository;
    private final TownService townService;
    private final TeamService teamService;
    private final StatService statService;
    private final ModelMapper modelMapper;
    private final Unmarshaller unmarshaller;
    private final Validator validator;
    Path path = Path.of("src/main/resources/files/xml/players.xml");

    @Autowired
    public PlayerServiceImpl(PlayerRepository playerRepository, TownService townService, TeamService teamService, StatService statService, ModelMapper modelMapper) throws JAXBException {
        this.playerRepository = playerRepository;
        this.townService = townService;
        this.teamService = teamService;
        this.statService = statService;
        this.modelMapper = modelMapper;
        JAXBContext context = JAXBContext.newInstance(ImportPlayerRootDTO.class);
        this.unmarshaller = context.createUnmarshaller();
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }


    @Override
    public boolean areImported() {
        return this.playerRepository.count() >0;
    }

    @Override
    public String readPlayersFileContent() throws IOException {
        return Files.readString(path);
    }

    @Override
    public String importPlayers() throws FileNotFoundException, JAXBException {
        ImportPlayerRootDTO players = (ImportPlayerRootDTO)
                this.unmarshaller.unmarshal(new FileReader(path.toAbsolutePath().toString()));

        return players.getPlayers().stream()
                .map(this::importPlayer).collect(Collectors.joining("\n"));
    }

    private String importPlayer(ImportPlayerDTO player) {
        Set<ConstraintViolation<ImportPlayerDTO>> validate
                = this.validator.validate(player);
        if (validate.isEmpty()){
            if (this.playerRepository.findByEmail(player.getEmail()).isEmpty()){
                Town town = this.townService.findByName(player.getTown().getName());
                Team team = this.teamService.findByName(player.getTeam().getName());
                Stat stat = this.statService.findById(player.getStat().getId());
                Player playerToAdd = this.modelMapper.map(player,Player.class);
                playerToAdd.setTown(town);
                playerToAdd.setTeam(team);
                playerToAdd.setStat(stat);
                this.playerRepository.save(playerToAdd);
                return String.format("Successfully imported Player %s %s - %s"
                ,playerToAdd.getFirstName(),playerToAdd.getLastName(),playerToAdd.getPosition());

            }else{
                return "Invalid player";
            }
        }else{
            return "Invalid player";
        }
    }

    @Override
    public String exportBestPlayers() {
        StringBuilder sb = new StringBuilder();
        LocalDate after = LocalDate.of(1995,1,1);
        LocalDate before = LocalDate.of(2003,1,1);
        List<Player> players = this.playerRepository.findAllByBirthDateAfterAndBirthDateBeforeOrderByStatShootingDescStatPassingDescStatEnduranceDescLastName(after,before);
        for (Player player : players) {
            sb.append(String.format("Player - %s %S%n" +
                            "\tposition -  %s%n" +
                            "\tTeam -  %s%n" +
                            "\tStadium -  %s%n",player.getFirstName(),player.getLastName()
            ,player.getPosition(),player.getTeam().getName(),player.getTeam().getStadiumName()));
        }

        return sb.toString();
    }
}
