package com.aston.userservice.handler;

import com.aston.userservice.domain.response.SimpleMessage;
import com.aston.userservice.exception.AuthException;
import com.aston.userservice.exception.UserNotFoundException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected SimpleMessage handleMethodArgumentNotValid(IllegalArgumentException exception) {
        return new SimpleMessage(exception.getMessage());
    }

    @ExceptionHandler(InvalidMediaTypeException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    protected SimpleMessage handleMethodArgumentNotValid(InvalidMediaTypeException exception) {
        return new SimpleMessage(exception.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected SimpleMessage handleUserNotFoundException(UserNotFoundException exception) {
        return new SimpleMessage(exception.getMessage());
    }

    @ExceptionHandler(AuthException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected SimpleMessage handleAuthException(AuthException exception) {
        return new SimpleMessage(exception.getMessage());
    }

    @ExceptionHandler(JwtException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected SimpleMessage handleJwtException(JwtException exception) {
        return new SimpleMessage(exception.getMessage());
    }
}
