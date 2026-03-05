package com.ayd2.congress.exceptionhandler;



import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ayd2.congress.exceptions.DuplicatedEntityException;
import com.ayd2.congress.exceptions.InsufficientFundsException;
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
    public String handleDuplicatedException(DuplicatedEntityException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(NotAuthorizedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleNotAutorizedException(NotAuthorizedException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(InvalidDateRangeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleInvalidException(InvalidDateRangeException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(InvalidPriceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleInvalidPriceExcepcion(InvalidPriceException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleArgumentNotValidException(MethodArgumentNotValidException ex) {
        if (ex.getBindingResult().getFieldErrors().isEmpty()) {
            return "Validation error";
        }

        var fe = ex.getBindingResult().getFieldErrors().get(0);
        return fe.getField() + ": " + fe.getDefaultMessage();
    }

    @ExceptionHandler(InsufficientFundsException.class)
    @ResponseStatus(HttpStatus.PAYMENT_REQUIRED)
    public String handleInsufficientFundsException(InsufficientFundsException ex) {
        return ex.getMessage();
    }

}
