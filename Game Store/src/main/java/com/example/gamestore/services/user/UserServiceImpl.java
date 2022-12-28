package com.example.gamestore.services.user;

import com.example.gamestore.constants.Validations;
import com.example.gamestore.domain.dtos.UserLogin;
import com.example.gamestore.domain.dtos.UserRegister;
import com.example.gamestore.domain.entities.User;
import com.example.gamestore.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private ModelMapper modelMapper = new ModelMapper();
    private User user;
    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public String registerUser(String[] args) {
       String email = args[1];
       String password = args[2];
       String confirmPassword = args[3];
       String fullName = args[4];

       UserRegister userRegisterDTO;
       try {
           userRegisterDTO = new UserRegister(email, password, confirmPassword, fullName);
       }catch (IllegalArgumentException exception){
           return exception.getMessage();
       }


        User user = this.modelMapper.map(userRegisterDTO, User.class);

        if (this.userRepository.count() ==0) {
            user.setAdmin(true);
        }
        boolean isUserFound = this.userRepository.findByEmail(userRegisterDTO.getEmail()).isPresent();

        if (isUserFound){
           return Validations.EMAIL_ALREADY_EXIST;
        }

        this.userRepository.save(user);
        return userRegisterDTO.successfulRegister();
    }

    @Override
    public String loginUser(String[] args) {
       String email = args[1];
       String password = args[2];
        UserLogin userLogin = new UserLogin(email,password);
       Optional<User> user = this.userRepository.findByEmail(userLogin.getEmail());

        if (user.isPresent() && this.user == null
                && user.get().getPassword().equals(userLogin.getPassword())){
            this.user = this.userRepository.findByEmail(userLogin.getEmail()).get();
            return "Successfully logged in " + this.user.getFullName();
        }
        return Validations.PASSWORD_NOT_VALID_MESSAGE;
    }

    @Override
    public String logoutUser() {
        if (this.user == null){
            return Validations.NO_USER_WAS_LOGGED_IN;
        }

        String outputMessage = "User" + this.user.getFullName() + " successfully logged out.";

        this.user = null;
        return outputMessage;
    }

    @Override
    public User getLoggedInUser() {
        return this.user;
    }


}
