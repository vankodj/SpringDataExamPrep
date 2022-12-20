package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.ImportPartDTO;
import softuni.exam.models.entity.Part;
import softuni.exam.repository.PartRepository;
import softuni.exam.service.PartService;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

@Service
public class PartServiceImpl implements PartService {
    private final PartRepository partRepository;
    private final Gson gson;
    private final ModelMapper modelMapper;
    private final StringBuilder sb;
    private final Validator validator;
    Path path = Path.of("src/main/resources/files/json/parts.json");

    @Autowired
    public PartServiceImpl(PartRepository partRepository, Gson gson, ModelMapper modelMapper, StringBuilder sb) {
        this.partRepository = partRepository;
        this.gson = gson;
        this.modelMapper = modelMapper;
        this.sb = sb;
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }


    @Override
    public boolean areImported() {
        return this.partRepository.count() >0;
    }

    @Override
    public String readPartsFileContent() throws IOException {
        return Files.readString(path);
    }

    @Override
    public String importParts() throws IOException {
        String json = this.readPartsFileContent();
        ImportPartDTO[] parts = this.gson.fromJson(json,ImportPartDTO[].class);
        for (ImportPartDTO part : parts) {
            Set<ConstraintViolation<ImportPartDTO>> validate
                    = this.validator.validate(part);
            if (validate.isEmpty()){
                if (this.partRepository.findByPartName(part.getPartName()).isEmpty()){
                    Part partToAdd = this.modelMapper.map(part,Part.class);
                    this.partRepository.save(partToAdd);
                    sb.append(String.format("Successfully imported part %s %.2f"
                    ,partToAdd.getPartName(),partToAdd.getPrice()))
                            .append(System.lineSeparator());
                }else{
                    sb.append("Invalid part").append(System.lineSeparator());
                }
            }else{
                sb.append("Invalid part").append(System.lineSeparator());
            }
        }


        return sb.toString();
    }

    @Override
    public Part findPartById(Long id) {
        return this.partRepository.findPartById(id).orElse(null);
    }
}
