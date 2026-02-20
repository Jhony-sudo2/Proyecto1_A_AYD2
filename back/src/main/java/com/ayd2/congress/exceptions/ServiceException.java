package com.ayd2.congress.exceptions;

public class ServiceException extends Exception {
    public ServiceException() {
    }
    public ServiceException(String message) {
        super(message);
    }
}
