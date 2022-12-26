package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.ImportAgentDTO;
import softuni.exam.models.entity.Agent;
import softuni.exam.models.entity.Town;
import softuni.exam.repository.AgentRepository;
import softuni.exam.service.AgentService;
import softuni.exam.service.TownService;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

@Service
public class AgentServiceImpl implements AgentService {
    private final AgentRepository agentRepository;
    private final TownService townService;
    private final Gson gson;
    private final ModelMapper modelMapper;
    private final Path path = Path.of("src/main/resources/files/json/agents.json");
    private final Validator validator;
    private final StringBuilder sb;

    @Autowired
    public AgentServiceImpl(AgentRepository agentRepository, TownService townService, Gson gson, ModelMapper modelMapper) {
        this.agentRepository = agentRepository;
        this.townService = townService;
        this.gson = gson;
        this.modelMapper = modelMapper;
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
        this.sb = new StringBuilder();
    }

    @Override
    public boolean areImported() {
        return this.agentRepository.count() >0;
    }

    @Override
    public String readAgentsFromFile() throws IOException {
        return Files.readString(path);
    }

    @Override
    public String importAgents() throws IOException {
        String json = this.readAgentsFromFile();
        ImportAgentDTO[] agents = this.gson.fromJson(json,ImportAgentDTO[].class);
        for (ImportAgentDTO agent : agents) {
            Set<ConstraintViolation<ImportAgentDTO>> validate
                    = this.validator.validate(agent);
            if (validate.isEmpty()){
                if (this.agentRepository.findByFirstName(agent.getFirstName()).isEmpty()){
                   Town town = this.townService.findByTownName(agent.getTown());
                    Agent agentToAdd = this.modelMapper.map(agent,Agent.class);
                    agentToAdd.setTown(town);
                    this.agentRepository.save(agentToAdd);
                    sb.append(String.format("Successfully imported agent - %s %s"
                    ,agentToAdd.getFirstName(),agentToAdd.getLastName()))
                            .append(System.lineSeparator());

                }else{
                    sb.append("Invalid agent").append(System.lineSeparator());
                }
            }else{
                sb.append("Invalid agent").append(System.lineSeparator());
            }
        }

        return sb.toString();
    }

    @Override
    public Agent findAgentByFirstName(String name) {
        return this.agentRepository.findAgentByFirstName(name).orElse(null);
    }
}
