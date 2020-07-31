package com.example.spotifyrest.exception;

import org.springframework.http.HttpStatus;

public class ServiceException extends RuntimeException {

    private final String message;
    private final HttpStatus httpStatus;

    public ServiceException(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

}
