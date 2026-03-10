package com.ayd2.congress.exceptions;

public class InsufficientFundsException extends ServiceException{
    public InsufficientFundsException(){
        super();
    }

    public InsufficientFundsException(String message){
        super(message);
    }
}
