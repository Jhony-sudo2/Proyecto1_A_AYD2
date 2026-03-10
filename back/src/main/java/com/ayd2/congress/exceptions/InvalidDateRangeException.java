package com.ayd2.congress.exceptions;

public class InvalidDateRangeException extends ServiceException{
    public InvalidDateRangeException(){
        super();
    }

    public InvalidDateRangeException(String message){
        super(message);
    }
}
