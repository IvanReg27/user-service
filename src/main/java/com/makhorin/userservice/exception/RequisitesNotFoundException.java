package com.aston.userservice.exception;

public class RequisitesNotFoundException extends RuntimeException {
    public RequisitesNotFoundException() {
    }

    public RequisitesNotFoundException(String message) {
        super(message);
    }
}
