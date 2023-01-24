package com.example.restservice.model.user.exceptions;

public class UserNotFoundException extends UserException {
    public UserNotFoundException(String email) {
        super(String.format("User with email `%s` not found", email));
    }
}
