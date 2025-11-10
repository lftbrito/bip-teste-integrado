package com.example.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Representação completa de um benefício")
public class BeneficioDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "ID único do benefício", example = "1")
    private Long id;

    @Schema(description = "Nome do benefício", example = "Vale Refeição", required = true)
    private String nome;

    @Schema(description = "Descrição detalhada do benefício", example = "Benefício para alimentação")
    private String descricao;

    @Schema(description = "Valor monetário do benefício", example = "1000.00", required = true)
    private BigDecimal valor;

    @Schema(description = "Indica se o benefício está ativo", example = "true", required = true)
    private Boolean ativo;

    @Schema(description = "Versão para controle de concorrência otimista", example = "0")
    private Long version;

    @Schema(description = "Data e hora de criação do benefício")
    private LocalDateTime createdAt;

    @Schema(description = "Data e hora da última atualização do benefício")
    private LocalDateTime updatedAt;

    public BeneficioDTO() {
    }

    public BeneficioDTO(Long id, String nome, String descricao, BigDecimal valor, Boolean ativo, Long version) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.valor = valor;
        this.ativo = ativo;
        this.version = version;
    }

    // Getters e Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "BeneficioDTO{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", valor=" + valor +
                ", ativo=" + ativo +
                ", version=" + version +
                '}';
    }
}
