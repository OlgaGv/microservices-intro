package com.learn.micro.songservice.exception;

public class GeneralFailureException extends RuntimeException {

    public GeneralFailureException(String message) {
        super(message);
    }

    public GeneralFailureException(Throwable cause) {
        super(cause);
    }

    public GeneralFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}
