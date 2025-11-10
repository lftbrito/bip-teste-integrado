package com.example.ejb.exception;

import jakarta.ejb.ApplicationException;

/**
 * Exception base para todas as exceções de negócio relacionadas a Benefícios.
 * 
 * <p>Anotada com @ApplicationException(rollback = true) para garantir
 * que qualquer exceção filha cause rollback automático da transação.
 * 
 * @author Sistema de Benefícios
 */
@ApplicationException(rollback = true)
public class BeneficioException extends Exception {
    
    private static final long serialVersionUID = 1L;
    
    public BeneficioException(String message) {
        super(message);
    }
    
    public BeneficioException(String message, Throwable cause) {
        super(message, cause);
    }
}
