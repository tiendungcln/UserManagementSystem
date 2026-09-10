package com.library.usermanagementsystem.exception;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ErrorResponse handleUserNotFound(UserNotFoundException ex){
        return new ErrorResponse(404, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleValidationException(MethodArgumentNotValidException ex){

        Map<String, String> errors = new HashMap<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()){
            errors.put(
                    error.getField(),
                    error.getDefaultMessage()
            );
        }

        return new ErrorResponse(400, "Validation failed", errors);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ErrorResponse handleBadCredentials(BadCredentialsException ex) {
        return new ErrorResponse(
                401,
                "Invalid username or password"
        );
    }

    @ExceptionHandler(RefreshTokenException.class)
    public ErrorResponse handleRefreshTokenException(
            RefreshTokenException ex
    ) {
        return new ErrorResponse(401, ex.getMessage());
    }

    @ExceptionHandler(DisabledException.class)
    public ErrorResponse handleDisabledException(
            DisabledException ex
    ) {
        return new ErrorResponse(
                403,
                "Please verify your email before logging in"
        );
    }

}
