package ru.makhorin.userservice.handler;

import ru.makhorin.userservice.domain.response.SimpleMessage;
import ru.makhorin.userservice.exception.UserNotFoundException;
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
}
