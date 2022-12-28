package com.example.gamestore.domain.dtos;

import com.example.gamestore.constants.Validations;

import java.util.regex.Pattern;

public class UserRegister {

    private String email;

    private String password;

    private String confirmPassword;

    private String fullName;

    public UserRegister(String email, String password, String confirmPassword, String fullName) {
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.fullName = fullName;
        this.validate();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    private void validate(){
        boolean isEmailValid = Pattern.matches(Validations.EMAIL_PATTERN, this.email);

        if (!isEmailValid){
            throw new IllegalArgumentException(Validations.EMAIL_NOT_VALID_MESSAGE);
        }

        boolean isPasswordValid = Pattern.matches(Validations.PASSWORD_PATTERN,this.password);

        if (!isPasswordValid) {
            throw new IllegalArgumentException(Validations.PASSWORD_NOT_VALID_MESSAGE);
        }
        if (!password.equals(confirmPassword)){
            throw new IllegalArgumentException(Validations.PASSWORD_NOT_MATCH);
        }



    }

public String successfulRegister(){
        return fullName + " was registered";
}
}
