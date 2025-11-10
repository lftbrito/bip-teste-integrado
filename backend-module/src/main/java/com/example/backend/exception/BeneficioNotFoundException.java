package com.example.backend.exception;

public class BeneficioNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public BeneficioNotFoundException(String message) {
        super(message);
    }

    public BeneficioNotFoundException(Long id) {
        super("Benefício não encontrado com ID: " + id);
    }
}
