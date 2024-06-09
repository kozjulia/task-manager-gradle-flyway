package com.example.exception;

public class NotSaveException extends RuntimeException {

    public NotSaveException(String message) {
        super(message);
    }

}