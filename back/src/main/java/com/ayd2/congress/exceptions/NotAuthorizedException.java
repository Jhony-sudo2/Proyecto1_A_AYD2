package com.ayd2.congress.exceptions;

public class NotAuthorizedException extends ServiceException{
    public NotAuthorizedException(){

    }
    public NotAuthorizedException(String message){
        super(message);
    }
}
