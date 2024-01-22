package com.sotska.exception;

import lombok.Getter;

@Getter
public class MoviesException extends RuntimeException {

    private ExceptionType exceptionType;

    public MoviesException(ExceptionType exceptionType, String message, Throwable cause) {
        super(message, cause);
        this.exceptionType  = exceptionType;
    }

    public MoviesException(ExceptionType exceptionType, String message) {
        super(message);
        this.exceptionType  = exceptionType;
    }

    public enum ExceptionType {
        CHILD_ENTITY_NOT_FOUND,
        ENTITY_NOT_FOUND
    }
}
