package com.modsen.exception_handler.exception;

public class UserAlreadyExistsException extends RuntimeException{

    public UserAlreadyExistsException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }

    public UserAlreadyExistsException(String errorMessage){
        super(errorMessage);
    }

}
