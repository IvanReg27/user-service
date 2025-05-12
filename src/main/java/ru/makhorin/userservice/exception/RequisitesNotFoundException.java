package ru.makhorin.userservice.exception;

public class RequisitesNotFoundException extends RuntimeException {
    public RequisitesNotFoundException() {
    }

    public RequisitesNotFoundException(String message) {
        super(message);
    }
}
