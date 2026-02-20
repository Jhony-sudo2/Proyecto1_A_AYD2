package com.ayd2.congress.exceptionhandler;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ayd2.congress.exceptions.NotFoundException;

@RestControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(org.springframework.http.HttpStatus.NOT_FOUND)
    public String handleNotFoundException(NotFoundException ex) {
        return ex.getMessage();
    }


}
