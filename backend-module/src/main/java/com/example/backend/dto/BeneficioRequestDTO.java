package com.example.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.math.BigDecimal;

@Schema(description = "Dados para criar ou atualizar um benefício")
public class BeneficioRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    @Schema(description = "Nome do benefício", example = "Vale Alimentação", required = true, minLength = 3, maxLength = 100)
    private String nome;

    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    @Schema(description = "Descrição detalhada do benefício", example = "Benefício para compras em supermercados", maxLength = 500)
    private String descricao;

    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.0", inclusive = true, message = "Valor deve ser maior ou igual a 0")
    @Schema(description = "Valor monetário do benefício", example = "500.00", required = true, minimum = "0")
    private BigDecimal valor;

    @NotNull(message = "Status ativo é obrigatório")
    @Schema(description = "Indica se o benefício está ativo", example = "true", required = true)
    private Boolean ativo;

    public BeneficioRequestDTO() {
    }

    public BeneficioRequestDTO(String nome, String descricao, BigDecimal valor, Boolean ativo) {
        this.nome = nome;
        this.descricao = descricao;
        this.valor = valor;
        this.ativo = ativo;
    }

    // Getters e Setters

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public String toString() {
        return "BeneficioRequestDTO{" +
                "nome='" + nome + '\'' +
                ", valor=" + valor +
                ", ativo=" + ativo +
                '}';
    }
}
