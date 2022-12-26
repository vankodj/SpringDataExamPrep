package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dtos.ImportPictureDTO;
import softuni.exam.models.entities.Car;
import softuni.exam.models.entities.Picture;
import softuni.exam.repository.PictureRepository;
import softuni.exam.service.CarService;
import softuni.exam.service.PictureService;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

@Service
public class PictureServiceImpl implements PictureService {
    private final PictureRepository pictureRepository;
    private final CarService carService;
    private final ModelMapper modelMapper;
    private final Gson gson;
    private final StringBuilder sb;
    private final Validator validator;
    Path path = Path.of("src/main/resources/files/json/pictures.json");

    public PictureServiceImpl(PictureRepository pictureRepository, CarService carService, ModelMapper modelMapper, Gson gson, StringBuilder sb) {
        this.pictureRepository = pictureRepository;
        this.carService = carService;
        this.modelMapper = modelMapper;
        this.gson = gson;
        this.sb = sb;
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Override
    public boolean areImported() {
        return this.pictureRepository.count() >0;
    }

    @Override
    public String readPicturesFromFile() throws IOException {
        return Files.readString(path);
    }

    @Override
    public String importPictures() throws IOException {
       String json = this.readPicturesFromFile();
        ImportPictureDTO[] pictures = this.gson.fromJson(json,ImportPictureDTO[].class);
        for (ImportPictureDTO picture : pictures) {
            Set<ConstraintViolation<ImportPictureDTO>> validate
                    = this.validator.validate(picture);
            if (validate.isEmpty()){
                if(this.pictureRepository.findByName(picture.getName()).isEmpty()){
                    Car car = this.carService.findCarById(picture.getCar());
                    Picture pictureToAdd = this.modelMapper.map(picture,Picture.class);
                    pictureToAdd.setCar(car);
                    this.pictureRepository.save(pictureToAdd);
                    sb.append(String.format("Successfully import picture - %s"
                    ,pictureToAdd.getName())).append(System.lineSeparator());
                }else{
                    sb.append("Invalid picture").append(System.lineSeparator());
                }
            }else{
                sb.append("Invalid picture").append(System.lineSeparator());
            }
        }


        return sb.toString();
    }

    @Override
    public List<Picture> findAllByCar(Car car) {
        return this.pictureRepository.findAllByCar(car).orElse(null);
    }
}
