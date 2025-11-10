package com.example.backend.exception;

public class BeneficioConflictException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public BeneficioConflictException(String message) {
        super(message);
    }

    public BeneficioConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
