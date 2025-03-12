package com.modsen.exception_handler.exception;

public class FileStorageException extends RuntimeException{

    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileStorageException(String message){
        super(message);
    }
}
