package com.example.ejb.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Entity representando um Benefício.
 * 
 * <p>Features:
 * <ul>
 *   <li>Optimistic Locking via campo VERSION</li>
 *   <li>Validações Bean Validation</li>
 *   <li>Soft delete via campo ATIVO</li>
 * </ul>
 * 
 * @author Sistema de Benefícios
 * @version 1.0
 */
@Entity
@Table(name = "BENEFICIO")
public class Beneficio implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    
    @Column(name = "NOME", nullable = false, length = 100)
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    private String nome;
    
    @Column(name = "DESCRICAO", length = 255)
    @Size(max = 255, message = "Descrição deve ter no máximo 255 caracteres")
    private String descricao;
    
    @Column(name = "VALOR", nullable = false, precision = 15, scale = 2)
    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.0", inclusive = true, message = "Valor não pode ser negativo")
    private BigDecimal valor;
    
    @Column(name = "ATIVO", nullable = false)
    @NotNull(message = "Campo ativo é obrigatório")
    private Boolean ativo = true;
    
    @Version
    @Column(name = "VERSION")
    private Long version;
    
    // ========== Constructors ==========
    
    public Beneficio() {
        this.ativo = true;
    }
    
    public Beneficio(String nome, String descricao, BigDecimal valor) {
        this.nome = nome;
        this.descricao = descricao;
        this.valor = valor;
        this.ativo = true;
    }
    
    public Beneficio(String nome, String descricao, BigDecimal valor, Boolean ativo) {
        this.nome = nome;
        this.descricao = descricao;
        this.valor = valor;
        this.ativo = ativo != null ? ativo : true;
    }
    
    // ========== Getters and Setters ==========
    
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
    
    // ========== Lifecycle Callbacks ==========
    
    /**
     * Valida que o valor não é negativo antes de persistir/atualizar.
     * Esta é uma validação adicional além das anotações Bean Validation.
     */
    @PrePersist
    @PreUpdate
    private void validateValor() {
        if (valor != null && valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Valor não pode ser negativo: " + valor);
        }
    }
    
    // ========== Object Methods ==========
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Beneficio that = (Beneficio) o;
        
        return id != null && id.equals(that.id);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
    
    @Override
    public String toString() {
        return String.format(
            "Beneficio[id=%d, nome='%s', valor=%s, ativo=%s, version=%d]",
            id, nome, valor, ativo, version
        );
    }
}
