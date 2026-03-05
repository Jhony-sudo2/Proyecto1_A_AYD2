package com.ayd2.congress.exceptions;

public class CongressNotStartedException extends ServiceException{
    public CongressNotStartedException(String message) {
        super(message);
    }
}
