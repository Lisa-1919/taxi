package com.modsen.account.exception;

public class CreateUserException extends RuntimeException{

    public CreateUserException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    public CreateUserException(String errorMessage) {
        super(errorMessage);
    }

}
