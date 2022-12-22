package softuni.exam.instagraphlite.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.instagraphlite.models.dtos.ImportPictureDTO;
import softuni.exam.instagraphlite.models.entities.Picture;
import softuni.exam.instagraphlite.repository.PictureRepository;
import softuni.exam.instagraphlite.service.PictureService;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

@Service
public class PictureServiceImpl implements PictureService {

    private final PictureRepository pictureRepository;
    private final Gson gson;
    private final ModelMapper modelMapper;
    private final Validator validator;


    @Autowired
    public PictureServiceImpl(PictureRepository pictureRepository, Gson gson, ModelMapper modelMapper) {
        this.pictureRepository = pictureRepository;
        this.gson = gson;
        this.modelMapper = modelMapper;
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Override
    public boolean areImported() {
        return this.pictureRepository.count() >0;
    }

    @Override
    public String readFromFileContent() throws IOException {
        Path path = Path.of("src/main/resources/files/pictures.json");
        return Files.readString(path);
    }

    @Override
    public String importPictures() throws IOException {
       StringBuilder sb = new StringBuilder();
        String json = this.readFromFileContent();
        ImportPictureDTO[] pictures = this.gson.fromJson(json,ImportPictureDTO[].class);
        for (ImportPictureDTO picture : pictures) {
            Set<ConstraintViolation<ImportPictureDTO>> validate
                    = this.validator.validate(picture);
            if (validate.isEmpty()){
                if(this.pictureRepository.findByPath(picture.getPath()).isEmpty()){
                    Picture pictureToAdd = this.modelMapper.map(picture,Picture.class);
                    this.pictureRepository.save(pictureToAdd);
                    sb.append(String.format("Successfully imported Picture, with size %.2f"
                    ,pictureToAdd.getSize())).append(System.lineSeparator());;
                }else{
                    sb.append("Invalid Picture").append(System.lineSeparator());
                }
            }else{
                sb.append("Invalid Picture").append(System.lineSeparator());
            }
        }


        return sb.toString();
    }

    @Override
    public String exportPictures() {
        return null;
    }

    @Override
    public Picture findPictureByPath(String path) {
        return this.pictureRepository.findPictureByPath(path).orElse(null);
    }
}
