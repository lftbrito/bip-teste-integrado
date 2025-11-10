package com.example.ejb.exception;

/**
 * Exception lançada quando um benefício não é encontrado no banco de dados.
 * 
 * @author Sistema de Benefícios
 */
public class BeneficioNaoEncontradoException extends BeneficioException {
    
    private static final long serialVersionUID = 1L;
    
    private final Long beneficioId;
    
    public BeneficioNaoEncontradoException(Long beneficioId) {
        super("Benefício não encontrado: " + beneficioId);
        this.beneficioId = beneficioId;
    }
    
    public Long getBeneficioId() {
        return beneficioId;
    }
}
