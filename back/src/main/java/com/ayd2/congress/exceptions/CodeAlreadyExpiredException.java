package com.ayd2.congress.exceptions;

public class CodeAlreadyExpiredException extends ServiceException{
    public CodeAlreadyExpiredException(String message){
        super(message);
    }
}
