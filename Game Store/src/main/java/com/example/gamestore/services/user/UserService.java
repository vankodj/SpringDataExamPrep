package com.example.gamestore.services.user;

import com.example.gamestore.domain.entities.User;
import org.springframework.stereotype.Service;

import java.util.Optional;


public interface UserService {
    String registerUser(String[] args);
    String loginUser(String[] args);


    String logoutUser();

    User getLoggedInUser();
}
