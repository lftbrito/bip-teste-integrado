package com.example.ejb.exception;

import java.math.BigDecimal;

/**
 * Exception lançada quando uma transferência é tentada mas o benefício
 * origem não possui saldo suficiente.
 * 
 * @author Sistema de Benefícios
 */
public class SaldoInsuficienteException extends BeneficioException {
    
    private static final long serialVersionUID = 1L;
    
    private final BigDecimal saldoDisponivel;
    private final BigDecimal valorSolicitado;
    
    public SaldoInsuficienteException(BigDecimal saldoDisponivel, BigDecimal valorSolicitado) {
        super(String.format(
            "Saldo insuficiente. Disponível: %s, Solicitado: %s",
            saldoDisponivel, valorSolicitado
        ));
        this.saldoDisponivel = saldoDisponivel;
        this.valorSolicitado = valorSolicitado;
    }
    
    public BigDecimal getSaldoDisponivel() {
        return saldoDisponivel;
    }
    
    public BigDecimal getValorSolicitado() {
        return valorSolicitado;
    }
}
