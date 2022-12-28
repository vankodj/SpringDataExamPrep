package com.example.gamestore.constants;

import com.example.gamestore.domain.dtos.GameDTO;

public enum Validations {
    ;
    public static final String EMAIL_PATTERN = "^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$";
    public static final String EMAIL_NOT_VALID_MESSAGE = "Incorrect email.";
    public static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{6,}$";
    public static final String PASSWORD_NOT_VALID_MESSAGE = "Incorrect username / password.";
    public static final String PASSWORD_NOT_MATCH = "Passwords are not matching.";
    public static final String COMMAND_NOT_FOUND_MESSAGE = "Command not found.";
    public static final String EMAIL_ALREADY_EXIST = "Email already exist.";
    public static final String NO_USER_WAS_LOGGED_IN = "Cannot log out. No user was logged in.";
    public static final String NO_VALID_GAME_TITLE = "Not a valid game title.";
    public static final String NO_VALID_PRICE = "Price should positive number.";
    public static final String NO_VALID_SIZE = "Size should positive number.";
    public static final String NO_VALID_TRAILER_ID = "Trailer ID should be exactly 11.";
    public static final String NO_VALID_THUMBNAIL_URL = "Link should begin with (http://) / (https://).";
    public static final String NO_VALID_DESCRIPTION_SIZE = "Description should be at least 20 symbols.";
    public static final String NO_ADMIN_RIGHTS= "Unknown admin rights .";
    public static final String NO_VALID_ID = "There is no game with such id.";
    public static final String NO_VALID_OPERATION= "There is no game with such id.";



}
