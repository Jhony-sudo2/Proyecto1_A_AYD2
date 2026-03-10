package com.ayd2.congress.exceptions;

public class InvalidPriceException extends ServiceException{
    public InvalidPriceException(){
        super();
    }

    public InvalidPriceException(String message){
        super(message);
    }
}
