package com.ayd2.congress.exceptions;

public class NotFoundException extends ServiceException {
    public NotFoundException() {
    }
    public NotFoundException(String message) {
        super(message);
    }
    
}
