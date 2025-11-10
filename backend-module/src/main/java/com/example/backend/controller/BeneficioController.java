package com.example.backend.controller;

import com.example.backend.dto.*;
import com.example.backend.service.BeneficioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/beneficios")
@Tag(name = "Benefícios", description = "API para gerenciamento de benefícios de funcionários")
public class BeneficioController {

    private static final Logger log = LoggerFactory.getLogger(BeneficioController.class);

    private final BeneficioService service;

    public BeneficioController(BeneficioService service) {
        this.service = service;
    }

    /**
     * Lista todos os benefícios.
     * 
     * @return lista de benefícios
     */
    @GetMapping
    @Operation(summary = "Listar todos os benefícios", 
               description = "Retorna uma lista com todos os benefícios cadastrados no sistema (ativos e inativos)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso",
                     content = @Content(mediaType = "application/json",
                                       schema = @Schema(implementation = BeneficioDTO.class)))
    })
    public ResponseEntity<List<BeneficioDTO>> findAll() {
        log.debug("GET /api/beneficios - Listar todos");
        List<BeneficioDTO> beneficios = service.findAll();
        return ResponseEntity.ok(beneficios);
    }

    /**
     * Lista apenas os benefícios ativos.
     * 
     * @return lista de benefícios ativos
     */
    @GetMapping("/ativos")
    @Operation(summary = "Listar benefícios ativos", 
               description = "Retorna uma lista com todos os benefícios ativos cadastrados no sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso",
                     content = @Content(mediaType = "application/json",
                                       schema = @Schema(implementation = BeneficioDTO.class)))
    })
    public ResponseEntity<List<BeneficioDTO>> findAllAtivos() {
        log.debug("GET /api/beneficios/ativos - Listar apenas ativos");
        List<BeneficioDTO> beneficios = service.findAllAtivos();
        return ResponseEntity.ok(beneficios);
    }

    /**
     * Busca um benefício por ID.
     * 
     * @param id ID do benefício
     * @return benefício encontrado
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar benefício por ID", 
               description = "Retorna os detalhes de um benefício específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Benefício encontrado",
                     content = @Content(mediaType = "application/json",
                                       schema = @Schema(implementation = BeneficioDTO.class))),
        @ApiResponse(responseCode = "404", description = "Benefício não encontrado",
                     content = @Content(mediaType = "application/json",
                                       schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<BeneficioDTO> findById(
            @Parameter(description = "ID do benefício", required = true, example = "1")
            @PathVariable Long id) {
        log.debug("GET /api/v1/beneficios/{} - Buscar por ID", id);
        BeneficioDTO beneficio = service.findById(id);
        return ResponseEntity.ok(beneficio);
    }

    /**
     * Cria um novo benefício.
     * 
     * @param requestDTO dados do novo benefício
     * @return benefício criado
     */
    @PostMapping
    @Operation(summary = "Criar novo benefício", 
               description = "Cria um novo benefício no sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Benefício criado com sucesso",
                     content = @Content(mediaType = "application/json",
                                       schema = @Schema(implementation = BeneficioDTO.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos",
                     content = @Content(mediaType = "application/json",
                                       schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "Conflito - nome já existe",
                     content = @Content(mediaType = "application/json",
                                       schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<BeneficioDTO> create(
            @Parameter(description = "Dados do novo benefício", required = true)
            @Valid @RequestBody BeneficioRequestDTO requestDTO) {
        log.info("POST /api/v1/beneficios - Criar: {}", requestDTO.getNome());
        BeneficioDTO created = service.create(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Atualiza um benefício existente.
     * 
     * @param id ID do benefício a atualizar
     * @param requestDTO novos dados do benefício
     * @return benefício atualizado
     */
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar benefício", 
               description = "Atualiza os dados de um benefício existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Benefício atualizado com sucesso",
                     content = @Content(mediaType = "application/json",
                                       schema = @Schema(implementation = BeneficioDTO.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos",
                     content = @Content(mediaType = "application/json",
                                       schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Benefício não encontrado",
                     content = @Content(mediaType = "application/json",
                                       schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "Conflito - nome já existe",
                     content = @Content(mediaType = "application/json",
                                       schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<BeneficioDTO> update(
            @Parameter(description = "ID do benefício", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Novos dados do benefício", required = true)
            @Valid @RequestBody BeneficioRequestDTO requestDTO) {
        log.info("PUT /api/v1/beneficios/{} - Atualizar", id);
        BeneficioDTO updated = service.update(id, requestDTO);
        return ResponseEntity.ok(updated);
    }

    /**
     * Exclui (soft delete) um benefício.
     * 
     * @param id ID do benefício a excluir
     * @return resposta sem conteúdo
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir benefício", 
               description = "Realiza soft delete de um benefício (marca como inativo)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Benefício excluído com sucesso"),
        @ApiResponse(responseCode = "404", description = "Benefício não encontrado",
                     content = @Content(mediaType = "application/json",
                                       schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID do benefício", required = true, example = "1")
            @PathVariable Long id) {
        log.info("DELETE /api/v1/beneficios/{} - Excluir", id);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Realiza transferência de valor entre benefícios.
     * 
     * @param requestDTO dados da transferência
     * @return resposta com detalhes da transferência
     */
    @PostMapping("/transferir")
    @Operation(summary = "Transferir valor entre benefícios", 
               description = "Transfere um valor do benefício de origem para o de destino")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transferência realizada com sucesso",
                     content = @Content(mediaType = "application/json",
                                       schema = @Schema(implementation = TransferenciaResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos",
                     content = @Content(mediaType = "application/json",
                                       schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Benefício não encontrado",
                     content = @Content(mediaType = "application/json",
                                       schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "Conflito - saldo insuficiente ou benefício inativo",
                     content = @Content(mediaType = "application/json",
                                       schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<TransferenciaResponseDTO> transfer(
            @Parameter(description = "Dados da transferência", required = true)
            @Valid @RequestBody TransferenciaRequestDTO requestDTO) {
        log.info("POST /api/v1/beneficios/transferir - {} -> {}, valor: {}", 
                requestDTO.getBeneficioOrigemId(), 
                requestDTO.getBeneficioDestinoId(), 
                requestDTO.getValor());
        TransferenciaResponseDTO response = service.transfer(requestDTO);
        return ResponseEntity.ok(response);
    }
}
