package com.example.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.math.BigDecimal;

@Schema(description = "Requisição para transferir valor entre benefícios")
public class TransferenciaRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "ID do benefício de origem é obrigatório")
    @Schema(description = "ID do benefício de origem", example = "1", required = true)
    private Long beneficioOrigemId;

    @NotNull(message = "ID do benefício de destino é obrigatório")
    @Schema(description = "ID do benefício de destino", example = "2", required = true)
    private Long beneficioDestinoId;

    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", inclusive = true, message = "Valor deve ser maior que 0")
    @Schema(description = "Valor a ser transferido", example = "300.00", required = true, minimum = "0.01")
    private BigDecimal valor;

    public TransferenciaRequestDTO() {
    }

    public TransferenciaRequestDTO(Long beneficioOrigemId, Long beneficioDestinoId, BigDecimal valor) {
        this.beneficioOrigemId = beneficioOrigemId;
        this.beneficioDestinoId = beneficioDestinoId;
        this.valor = valor;
    }

    // Getters e Setters

    public Long getBeneficioOrigemId() {
        return beneficioOrigemId;
    }

    public void setBeneficioOrigemId(Long beneficioOrigemId) {
        this.beneficioOrigemId = beneficioOrigemId;
    }

    public Long getBeneficioDestinoId() {
        return beneficioDestinoId;
    }

    public void setBeneficioDestinoId(Long beneficioDestinoId) {
        this.beneficioDestinoId = beneficioDestinoId;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return "TransferenciaRequestDTO{" +
                "beneficioOrigemId=" + beneficioOrigemId +
                ", beneficioDestinoId=" + beneficioDestinoId +
                ", valor=" + valor +
                '}';
    }
}
