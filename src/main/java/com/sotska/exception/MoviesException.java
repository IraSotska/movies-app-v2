package com.sotska.exception;

import lombok.Getter;

@Getter
public class MoviesException extends RuntimeException {

    private final ExceptionType exceptionType;

    public MoviesException(ExceptionType exceptionType, String message) {
        super(message);
        this.exceptionType  = exceptionType;
    }

    public enum ExceptionType {
        NOT_FOUND,
        TIMEOUT
    }
}
