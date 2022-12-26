package exam.service.impl;

import exam.model.dtos.ImportTownDTO;
import exam.model.dtos.ImportTownRootDTO;
import exam.model.entities.Town;
import exam.repository.TownRepository;
import exam.service.TownService;
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
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TownServiceImpl implements TownService {
    private final TownRepository townRepository;
    private final Unmarshaller unmarshaller;
    private final Validator validator;
    private final ModelMapper modelMapper;
    Path path = Path.of("src/main/resources/files/xml/towns.xml");

   @Autowired
    public TownServiceImpl(TownRepository townRepository) throws JAXBException {
        this.townRepository = townRepository;
        JAXBContext context = JAXBContext.newInstance(ImportTownRootDTO.class);
        this.unmarshaller = context.createUnmarshaller();
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
        this.modelMapper = new ModelMapper();
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
    public String importTowns() throws JAXBException, FileNotFoundException {
        ImportTownRootDTO townDTOs = (ImportTownRootDTO)
                this.unmarshaller.unmarshal(new FileReader(path.toAbsolutePath().toString()));

        return townDTOs.getTowns().stream()
                .map(this::importTown)
                .collect(Collectors.joining("\n"));
    }

    private String importTown(ImportTownDTO dto) {
        Set<ConstraintViolation<ImportTownDTO>> validate = this.validator.validate(dto);
if (validate.isEmpty()){
    if (this.townRepository.findByName(dto.getName()).isEmpty()){
        Town townToAdd = this.modelMapper.map(dto,Town.class);
        this.townRepository.save(townToAdd);
       return String.format("Successfully imported Town %s",townToAdd.getName());
    }else{
        return "Invalid town";
    }
}else{
   return "Invalid town";
}
    }
@Override
    public Town findByName(String name) {
        return this.townRepository.findByName(name).orElse(null);
    }

}
