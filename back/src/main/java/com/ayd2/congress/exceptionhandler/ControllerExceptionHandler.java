package com.ayd2.congress.exceptionhandler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ayd2.congress.exceptions.DuplicatedEntityException;
import com.ayd2.congress.exceptions.InvalidDateRangeException;
import com.ayd2.congress.exceptions.InvalidPriceException;
import com.ayd2.congress.exceptions.NotAuthorizedException;
import com.ayd2.congress.exceptions.NotFoundException;

@RestControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(org.springframework.http.HttpStatus.NOT_FOUND)
    public String handleNotFoundException(NotFoundException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(DuplicatedEntityException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleDuplicatedException(DuplicatedEntityException ex){
        return ex.getMessage();
    }

    @ExceptionHandler(NotAuthorizedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleNotAutorizedException(NotAuthorizedException ex){
        return ex.getMessage();
    }

    @ExceptionHandler(InvalidDateRangeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleInvalidException(InvalidDateRangeException ex){
        return ex.getMessage();
    }

    @ExceptionHandler(InvalidPriceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleInvalidPriceExcepcion(InvalidPriceException ex){
        return ex.getMessage();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleArgumentNotValidException(MethodArgumentNotValidException ex){
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
          .forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }


}
