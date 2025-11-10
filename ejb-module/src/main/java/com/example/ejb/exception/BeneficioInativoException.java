package com.example.ejb.exception;

/**
 * Exception lançada quando uma operação é tentada em um benefício inativo.
 * 
 * @author Sistema de Benefícios
 */
public class BeneficioInativoException extends BeneficioException {
    
    private static final long serialVersionUID = 1L;
    
    private final Long beneficioId;
    private final String tipo;
    
    public BeneficioInativoException(Long beneficioId, String tipo) {
        super(String.format("Benefício %s está inativo: %d", tipo, beneficioId));
        this.beneficioId = beneficioId;
        this.tipo = tipo;
    }
    
    public Long getBeneficioId() {
        return beneficioId;
    }
    
    public String getTipo() {
        return tipo;
    }
}
