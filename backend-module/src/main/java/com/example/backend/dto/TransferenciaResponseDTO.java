package com.example.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Resposta detalhada de uma transferência realizada")
public class TransferenciaResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "Indica se a transferência foi bem-sucedida", example = "true")
    private Boolean sucesso;

    @Schema(description = "Mensagem descritiva do resultado", example = "Transferência realizada com sucesso")
    private String mensagem;

    @Schema(description = "Detalhes da transação realizada")
    private TransacaoDTO transacao;

    public TransferenciaResponseDTO() {
    }

    public TransferenciaResponseDTO(Boolean sucesso, String mensagem, TransacaoDTO transacao) {
        this.sucesso = sucesso;
        this.mensagem = mensagem;
        this.transacao = transacao;
    }

    // Getters e Setters

    public Boolean getSucesso() {
        return sucesso;
    }

    public void setSucesso(Boolean sucesso) {
        this.sucesso = sucesso;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public TransacaoDTO getTransacao() {
        return transacao;
    }

    public void setTransacao(TransacaoDTO transacao) {
        this.transacao = transacao;
    }

    /**
     * DTO interno representando os detalhes da transação.
     */
    @Schema(description = "Detalhes da transação de transferência")
    public static class TransacaoDTO implements Serializable {

        private static final long serialVersionUID = 1L;

        @Schema(description = "ID do benefício de origem", example = "1")
        private Long beneficioOrigemId;

        @Schema(description = "ID do benefício de destino", example = "2")
        private Long beneficioDestinoId;

        @Schema(description = "Valor transferido", example = "300.00")
        private BigDecimal valor;

        @Schema(description = "Saldo anterior do benefício de origem", example = "1000.00")
        private BigDecimal saldoAnteriorOrigem;

        @Schema(description = "Saldo novo do benefício de origem", example = "700.00")
        private BigDecimal saldoNovoOrigem;

        @Schema(description = "Saldo anterior do benefício de destino", example = "500.00")
        private BigDecimal saldoAnteriorDestino;

        @Schema(description = "Saldo novo do benefício de destino", example = "800.00")
        private BigDecimal saldoNovoDestino;

        @Schema(description = "Timestamp da transferência", example = "2025-11-08T10:30:00")
        private LocalDateTime timestamp;

        public TransacaoDTO() {
        }

        public TransacaoDTO(Long beneficioOrigemId, Long beneficioDestinoId, BigDecimal valor,
                          BigDecimal saldoAnteriorOrigem, BigDecimal saldoNovoOrigem,
                          BigDecimal saldoAnteriorDestino, BigDecimal saldoNovoDestino) {
            this.beneficioOrigemId = beneficioOrigemId;
            this.beneficioDestinoId = beneficioDestinoId;
            this.valor = valor;
            this.saldoAnteriorOrigem = saldoAnteriorOrigem;
            this.saldoNovoOrigem = saldoNovoOrigem;
            this.saldoAnteriorDestino = saldoAnteriorDestino;
            this.saldoNovoDestino = saldoNovoDestino;
            this.timestamp = LocalDateTime.now();
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

        // Alias methods for compatibility
        public BigDecimal getValorTransferido() {
            return valor;
        }

        public void setValorTransferido(BigDecimal valor) {
            this.valor = valor;
        }

        public BigDecimal getSaldoAnteriorOrigem() {
            return saldoAnteriorOrigem;
        }

        public void setSaldoAnteriorOrigem(BigDecimal saldoAnteriorOrigem) {
            this.saldoAnteriorOrigem = saldoAnteriorOrigem;
        }

        public BigDecimal getSaldoNovoOrigem() {
            return saldoNovoOrigem;
        }

        public void setSaldoNovoOrigem(BigDecimal saldoNovoOrigem) {
            this.saldoNovoOrigem = saldoNovoOrigem;
        }

        public BigDecimal getSaldoAnteriorDestino() {
            return saldoAnteriorDestino;
        }

        public void setSaldoAnteriorDestino(BigDecimal saldoAnteriorDestino) {
            this.saldoAnteriorDestino = saldoAnteriorDestino;
        }

        public BigDecimal getSaldoNovoDestino() {
            return saldoNovoDestino;
        }

        public void setSaldoNovoDestino(BigDecimal saldoNovoDestino) {
            this.saldoNovoDestino = saldoNovoDestino;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
        }
    }

    @Override
    public String toString() {
        return "TransferenciaResponseDTO{" +
                "sucesso=" + sucesso +
                ", mensagem='" + mensagem + '\'' +
                ", transacao=" + transacao +
                '}';
    }
}
