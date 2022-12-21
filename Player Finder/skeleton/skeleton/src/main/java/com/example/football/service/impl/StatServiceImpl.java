package com.example.football.service.impl;

import com.example.football.models.dto.ImportStatDTO;
import com.example.football.models.dto.ImportStatRootDTO;
import com.example.football.models.entity.Stat;
import com.example.football.repository.StatRepository;
import com.example.football.service.StatService;
import org.modelmapper.ModelMapper;
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
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StatServiceImpl implements StatService {
    private final StatRepository statRepository;
    private final ModelMapper modelMapper;
    private final Unmarshaller unmarshaller;
    private final Validator validator;
    Path path = Path.of("src/main/resources/files/xml/stats.xml");

    public StatServiceImpl(StatRepository statRepository, ModelMapper modelMapper) throws JAXBException {
        this.statRepository = statRepository;
        this.modelMapper = modelMapper;
        JAXBContext context = JAXBContext.newInstance(ImportStatRootDTO.class);
        this.unmarshaller = context.createUnmarshaller();
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }


    @Override
    public boolean areImported() {
        return this.statRepository.count() >0;
    }

    @Override
    public String readStatsFileContent() throws IOException {
        return Files.readString(path);
    }

    @Override
    public String importStats() throws FileNotFoundException, JAXBException {
        ImportStatRootDTO stats = (ImportStatRootDTO)
                this.unmarshaller.unmarshal(new FileReader(path.toAbsolutePath().toString()));

        return stats.getStats().stream()
                .map(this::importStat).collect(Collectors.joining("\n"));
    }

    @Override
    public Stat findById(Long id) {
        return this.statRepository.findById(id).orElse(null);
    }



    private String importStat(ImportStatDTO stat) {
        Set<ConstraintViolation<ImportStatDTO>> validate
                = this.validator.validate(stat);
        if (validate.isEmpty()){
            if (this.statRepository.findByPassingAndShootingAndEndurance(stat.getPassing()
            ,stat.getShooting(),stat.getEndurance()).isEmpty()){
                Stat statToAdd = this.modelMapper.map(stat,Stat.class);
                this.statRepository.save(statToAdd);
                return String.format("Successfully imported Stat %.2f - %.2f - %.2f"
                ,statToAdd.getPassing(),statToAdd.getShooting(),statToAdd.getEndurance());
            }else{
                return "Invalid stat";
            }
        }else{
            return "Invalid stat";
        }

    }
}
