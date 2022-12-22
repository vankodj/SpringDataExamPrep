package softuni.exam.instagraphlite.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.instagraphlite.models.dtos.ImportUserDTO;
import softuni.exam.instagraphlite.models.entities.Picture;
import softuni.exam.instagraphlite.models.entities.User;
import softuni.exam.instagraphlite.repository.UserRepository;
import softuni.exam.instagraphlite.service.PictureService;
import softuni.exam.instagraphlite.service.UserService;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PictureService pictureService;
    private final Gson gson;
    private final ModelMapper modelMapper;
    private final Validator validator;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PictureService pictureService, Gson gson, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.pictureService = pictureService;
        this.gson = gson;
        this.modelMapper = modelMapper;
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Override
    public boolean areImported() {
        return this.userRepository.count() >0;
    }

    @Override
    public String readFromFileContent() throws IOException {
        Path path = Path.of("src/main/resources/files/users.json");
        return Files.readString(path);
    }

    @Override
    public String importUsers() throws IOException {
       StringBuilder sb = new StringBuilder();
       String json = this.readFromFileContent();
        ImportUserDTO[] users = this.gson.fromJson(json,ImportUserDTO[].class);
        for (ImportUserDTO user : users) {
            Set<ConstraintViolation<ImportUserDTO>> validate
                    = this.validator.validate(user);
            if (validate.isEmpty()){
                if (this.userRepository.findByUsername(user.getUsername()).isEmpty()){
                    Picture picture = this.pictureService.findPictureByPath(user.getProfilePicture());
                    User userToAdd = this.modelMapper.map(user,User.class);
                    userToAdd.setProfilePicture(picture);
                    this.userRepository.save(userToAdd);
                    sb.append(String.format("Successfully imported User: %s"
                    ,userToAdd.getUsername())).append(System.lineSeparator());
                }else{
                    sb.append("Invalid user").append(System.lineSeparator());
                }
            }else{
                sb.append("Invalid user").append(System.lineSeparator());
            }
        }


        return sb.toString();
    }

    @Override
    public String exportUsersWithTheirPosts() {
        return null;
    }

    @Override
    public User findUserByUsername(String username) {
        return this.userRepository.findUserByUsername(username).orElse(null);
    }
}
