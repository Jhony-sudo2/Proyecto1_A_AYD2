package com.ayd2.congress.exceptions;

public class ExpiredJwtException extends ServiceException{
    public ExpiredJwtException(){

    }

    public ExpiredJwtException(String message){
        super(message);
    }

}
