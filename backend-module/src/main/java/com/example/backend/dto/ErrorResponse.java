package com.example.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "Resposta de erro padronizada da API")
public class ErrorResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "Timestamp do erro", example = "2025-11-08T10:30:00")
    private LocalDateTime timestamp;

    @Schema(description = "Código de status HTTP", example = "404")
    private Integer status;

    @Schema(description = "Nome do erro HTTP", example = "Not Found")
    private String error;

    @Schema(description = "Mensagem descritiva do erro", example = "Benefício não encontrado com ID: 999")
    private String message;

    @Schema(description = "Path da requisição que gerou o erro", example = "/api/v1/beneficios/999")
    private String path;

    @Schema(description = "Lista de erros de validação (quando aplicável)")
    private List<FieldError> errors;

    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
        this.errors = new ArrayList<>();
    }

    public ErrorResponse(Integer status, String error, String message, String path) {
        this();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    /**
     * Adiciona um erro de validação de campo.
     * 
     * @param field nome do campo com erro
     * @param message mensagem de erro
     */
    public void addFieldError(String field, String message) {
        if (this.errors == null) {
            this.errors = new ArrayList<>();
        }
        this.errors.add(new FieldError(field, message));
    }

    // Getters e Setters

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<FieldError> getErrors() {
        return errors;
    }

    public void setErrors(List<FieldError> errors) {
        this.errors = errors;
    }

    /**
     * DTO interno representando um erro de validação de campo.
     */
    @Schema(description = "Erro de validação de um campo específico")
    public static class FieldError implements Serializable {

        private static final long serialVersionUID = 1L;

        @Schema(description = "Nome do campo com erro", example = "nome")
        private String field;

        @Schema(description = "Mensagem de erro", example = "Nome é obrigatório")
        private String message;

        public FieldError() {
        }

        public FieldError(String field, String message) {
            this.field = field;
            this.message = message;
        }

        // Getters e Setters

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "timestamp=" + timestamp +
                ", status=" + status +
                ", error='" + error + '\'' +
                ", message='" + message + '\'' +
                ", path='" + path + '\'' +
                ", errors=" + errors +
                '}';
    }
}
