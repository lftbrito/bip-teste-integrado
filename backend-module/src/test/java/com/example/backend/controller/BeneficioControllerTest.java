package com.example.backend.controller;

import com.example.backend.dto.BeneficioDTO;
import com.example.backend.dto.BeneficioRequestDTO;
import com.example.backend.dto.TransferenciaRequestDTO;
import com.example.backend.dto.TransferenciaResponseDTO;
import com.example.backend.exception.BeneficioConflictException;
import com.example.backend.exception.BeneficioNotFoundException;
import com.example.backend.repository.BeneficioRepository;
import com.example.backend.service.BeneficioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = BeneficioController.class,
        excludeAutoConfiguration = {
                DataSourceAutoConfiguration.class, 
                HibernateJpaAutoConfiguration.class
        }
)
@ComponentScan(basePackages = "com.example.backend.controller",
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, 
                classes = {BeneficioRepository.class, com.example.backend.mapper.BeneficioMapper.class})
)
@DisplayName("BeneficioController - Testes Unitários")
class BeneficioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BeneficioService beneficioService;

    private BeneficioDTO beneficioDTO;
    private BeneficioRequestDTO beneficioRequestDTO;

    @BeforeEach
    void setUp() {
        beneficioDTO = new BeneficioDTO();
        beneficioDTO.setId(1L);
        beneficioDTO.setNome("Vale Refeição");
        beneficioDTO.setDescricao("Benefício para alimentação");
        beneficioDTO.setValor(new BigDecimal("1000.00"));
        beneficioDTO.setAtivo(true);
        beneficioDTO.setVersion(1L);
        beneficioDTO.setCreatedAt(LocalDateTime.now());
        beneficioDTO.setUpdatedAt(LocalDateTime.now());

        beneficioRequestDTO = new BeneficioRequestDTO();
        beneficioRequestDTO.setNome("Vale Refeição");
        beneficioRequestDTO.setDescricao("Benefício para alimentação");
        beneficioRequestDTO.setValor(new BigDecimal("1000.00"));
        beneficioRequestDTO.setAtivo(true);
    }

    @Test
    @DisplayName("GET /api/beneficios - Deve retornar lista vazia com status 200")
    void testFindAll_EmptyList() throws Exception {
        when(beneficioService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/beneficios")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(beneficioService, times(1)).findAll();
    }

    @Test
    @DisplayName("GET /api/beneficios - Deve retornar lista com 2 benefícios")
    void testFindAll_WithBeneficios() throws Exception {
        BeneficioDTO beneficio2 = new BeneficioDTO();
        beneficio2.setId(2L);
        beneficio2.setNome("Vale Transporte");
        beneficio2.setDescricao("Benefício para transporte");
        beneficio2.setValor(new BigDecimal("500.00"));
        beneficio2.setAtivo(true);

        List<BeneficioDTO> beneficios = Arrays.asList(beneficioDTO, beneficio2);
        when(beneficioService.findAll()).thenReturn(beneficios);

        mockMvc.perform(get("/api/beneficios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].nome", is("Vale Refeição")))
                .andExpect(jsonPath("$[0].valor", is(1000.00)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].nome", is("Vale Transporte")))
                .andExpect(jsonPath("$[1].valor", is(500.00)));

        verify(beneficioService, times(1)).findAll();
    }

    @Test
    @DisplayName("GET /api/beneficios/ativos - Deve retornar lista vazia quando não há ativos")
    void testFindAllAtivos_EmptyList() throws Exception {
        when(beneficioService.findAllAtivos()).thenReturn(List.of());

        mockMvc.perform(get("/api/beneficios/ativos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(beneficioService, times(1)).findAllAtivos();
    }

    @Test
    @DisplayName("GET /api/beneficios/ativos - Deve retornar apenas benefícios ativos")
    void testFindAllAtivos_WithBeneficios() throws Exception {
        BeneficioDTO beneficioAtivo1 = new BeneficioDTO();
        beneficioAtivo1.setId(1L);
        beneficioAtivo1.setNome("Vale Refeição");
        beneficioAtivo1.setDescricao("Benefício ativo 1");
        beneficioAtivo1.setValor(new BigDecimal("1000.00"));
        beneficioAtivo1.setAtivo(true);

        BeneficioDTO beneficioAtivo2 = new BeneficioDTO();
        beneficioAtivo2.setId(2L);
        beneficioAtivo2.setNome("Vale Transporte");
        beneficioAtivo2.setDescricao("Benefício ativo 2");
        beneficioAtivo2.setValor(new BigDecimal("500.00"));
        beneficioAtivo2.setAtivo(true);

        List<BeneficioDTO> beneficiosAtivos = Arrays.asList(beneficioAtivo1, beneficioAtivo2);
        when(beneficioService.findAllAtivos()).thenReturn(beneficiosAtivos);

        mockMvc.perform(get("/api/beneficios/ativos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].nome", is("Vale Refeição")))
                .andExpect(jsonPath("$[0].ativo", is(true)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].nome", is("Vale Transporte")))
                .andExpect(jsonPath("$[1].ativo", is(true)));

        verify(beneficioService, times(1)).findAllAtivos();
    }

    @Test
    @DisplayName("GET /api/beneficios/ativos - Deve retornar apenas ativos, não inativos")
    void testFindAllAtivos_OnlyActive() throws Exception {
        BeneficioDTO beneficioAtivo = new BeneficioDTO();
        beneficioAtivo.setId(1L);
        beneficioAtivo.setNome("Benefício Ativo");
        beneficioAtivo.setDescricao("Apenas este está ativo");
        beneficioAtivo.setValor(new BigDecimal("800.00"));
        beneficioAtivo.setAtivo(true);

        when(beneficioService.findAllAtivos()).thenReturn(Arrays.asList(beneficioAtivo));

        mockMvc.perform(get("/api/beneficios/ativos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].ativo", is(true)));

        verify(beneficioService, times(1)).findAllAtivos();
    }

    @Test
    @DisplayName("GET /api/beneficios/{id} - Deve retornar benefício existente com status 200")
    void testFindById_Success() throws Exception {
        when(beneficioService.findById(1L)).thenReturn(beneficioDTO);

        mockMvc.perform(get("/api/beneficios/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nome", is("Vale Refeição")))
                .andExpect(jsonPath("$.descricao", is("Benefício para alimentação")))
                .andExpect(jsonPath("$.valor", is(1000.00)))
                .andExpect(jsonPath("$.ativo", is(true)));

        verify(beneficioService, times(1)).findById(1L);
    }

    @Test
    @DisplayName("GET /api/beneficios/{id} - Deve retornar 404 quando benefício não existe")
    void testFindById_NotFound() throws Exception {
        when(beneficioService.findById(999L))
                .thenThrow(new BeneficioNotFoundException(999L));

        mockMvc.perform(get("/api/beneficios/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", containsString("999")));

        verify(beneficioService, times(1)).findById(999L);
    }

    @Test
    @DisplayName("POST /api/beneficios - Deve criar benefício com status 201")
    void testCreate_Success() throws Exception {
        when(beneficioService.create(any(BeneficioRequestDTO.class)))
                .thenReturn(beneficioDTO);

        mockMvc.perform(post("/api/beneficios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beneficioRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nome", is("Vale Refeição")))
                .andExpect(jsonPath("$.valor", is(1000.00)));

        verify(beneficioService, times(1)).create(any(BeneficioRequestDTO.class));
    }

    @Test
    @DisplayName("POST /api/beneficios - Deve retornar 400 quando nome está vazio")
    void testCreate_ValidationError_NomeVazio() throws Exception {
        beneficioRequestDTO.setNome("");

        mockMvc.perform(post("/api/beneficios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beneficioRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.errors", notNullValue()));

        verify(beneficioService, never()).create(any());
    }

    @Test
    @DisplayName("POST /api/beneficios - Deve retornar 400 quando nome é muito curto")
    void testCreate_ValidationError_NomeMuitoCurto() throws Exception {
        beneficioRequestDTO.setNome("AB");

        mockMvc.perform(post("/api/beneficios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beneficioRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.errors", hasSize(greaterThan(0))));

        verify(beneficioService, never()).create(any());
    }

    @Test
    @DisplayName("POST /api/beneficios - Deve retornar 400 quando valor é negativo")
    void testCreate_ValidationError_ValorNegativo() throws Exception {
        beneficioRequestDTO.setValor(new BigDecimal("-100.00"));

        mockMvc.perform(post("/api/beneficios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beneficioRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)));

        verify(beneficioService, never()).create(any());
    }

    @Test
    @DisplayName("POST /api/beneficios - Deve retornar 400 quando valor é null")
    void testCreate_ValidationError_ValorNull() throws Exception {
        beneficioRequestDTO.setValor(null);

        mockMvc.perform(post("/api/beneficios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beneficioRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)));

        verify(beneficioService, never()).create(any());
    }

    @Test
    @DisplayName("POST /api/beneficios - Deve retornar 409 quando nome já existe")
    void testCreate_Conflict_NomeDuplicado() throws Exception {
        when(beneficioService.create(any(BeneficioRequestDTO.class)))
                .thenThrow(new BeneficioConflictException("Já existe um benefício com o nome: Vale Refeição"));

        mockMvc.perform(post("/api/beneficios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beneficioRequestDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.error", is("Conflict")))
                .andExpect(jsonPath("$.message", containsString("Vale Refeição")));

        verify(beneficioService, times(1)).create(any(BeneficioRequestDTO.class));
    }

    @Test
    @DisplayName("PUT /api/beneficios/{id} - Deve atualizar benefício com status 200")
    void testUpdate_Success() throws Exception {
        beneficioDTO.setNome("Vale Refeição Atualizado");
        beneficioDTO.setValor(new BigDecimal("1200.00"));

        when(beneficioService.update(eq(1L), any(BeneficioRequestDTO.class)))
                .thenReturn(beneficioDTO);

        mockMvc.perform(put("/api/beneficios/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beneficioRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nome", is("Vale Refeição Atualizado")))
                .andExpect(jsonPath("$.valor", is(1200.00)));

        verify(beneficioService, times(1)).update(eq(1L), any(BeneficioRequestDTO.class));
    }

    @Test
    @DisplayName("PUT /api/beneficios/{id} - Deve retornar 404 quando benefício não existe")
    void testUpdate_NotFound() throws Exception {
        when(beneficioService.update(eq(999L), any(BeneficioRequestDTO.class)))
                .thenThrow(new BeneficioNotFoundException(999L));

        mockMvc.perform(put("/api/beneficios/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beneficioRequestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", containsString("999")));

        verify(beneficioService, times(1)).update(eq(999L), any(BeneficioRequestDTO.class));
    }

    @Test
    @DisplayName("PUT /api/beneficios/{id} - Deve retornar 400 com validação falha")
    void testUpdate_ValidationError() throws Exception {
        beneficioRequestDTO.setNome("AB");

        mockMvc.perform(put("/api/beneficios/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beneficioRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)));

        verify(beneficioService, never()).update(any(), any());
    }

    @Test
    @DisplayName("DELETE /api/beneficios/{id} - Deve excluir benefício com status 204")
    void testDelete_Success() throws Exception {
        doNothing().when(beneficioService).delete(1L);

        mockMvc.perform(delete("/api/beneficios/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(beneficioService, times(1)).delete(1L);
    }

    @Test
    @DisplayName("DELETE /api/beneficios/{id} - Deve retornar 404 quando benefício não existe")
    void testDelete_NotFound() throws Exception {
        doThrow(new BeneficioNotFoundException(999L))
                .when(beneficioService).delete(999L);

        mockMvc.perform(delete("/api/beneficios/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", containsString("999")));

        verify(beneficioService, times(1)).delete(999L);
    }

    @Test
    @DisplayName("POST /api/beneficios/transferir - Deve transferir valor com status 200")
    void testTransfer_Success() throws Exception {
        TransferenciaRequestDTO requestDTO = new TransferenciaRequestDTO();
        requestDTO.setBeneficioOrigemId(1L);
        requestDTO.setBeneficioDestinoId(2L);
        requestDTO.setValor(new BigDecimal("300.00"));

        TransferenciaResponseDTO responseDTO = new TransferenciaResponseDTO();
        responseDTO.setSucesso(true);
        responseDTO.setMensagem("Transferência realizada com sucesso");

        TransferenciaResponseDTO.TransacaoDTO transacao = new TransferenciaResponseDTO.TransacaoDTO();
        transacao.setBeneficioOrigemId(1L);
        transacao.setBeneficioDestinoId(2L);
        transacao.setValorTransferido(new BigDecimal("300.00"));
        transacao.setSaldoAnteriorOrigem(new BigDecimal("1000.00"));
        transacao.setSaldoNovoOrigem(new BigDecimal("700.00"));
        transacao.setSaldoAnteriorDestino(new BigDecimal("500.00"));
        transacao.setSaldoNovoDestino(new BigDecimal("800.00"));
        transacao.setTimestamp(LocalDateTime.now());

        responseDTO.setTransacao(transacao);

        when(beneficioService.transfer(any(TransferenciaRequestDTO.class)))
                .thenReturn(responseDTO);

        mockMvc.perform(post("/api/beneficios/transferir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sucesso", is(true)))
                .andExpect(jsonPath("$.mensagem", is("Transferência realizada com sucesso")))
                .andExpect(jsonPath("$.transacao.beneficioOrigemId", is(1)))
                .andExpect(jsonPath("$.transacao.beneficioDestinoId", is(2)))
                .andExpect(jsonPath("$.transacao.valorTransferido", is(300.00)))
                .andExpect(jsonPath("$.transacao.saldoNovoOrigem", is(700.00)))
                .andExpect(jsonPath("$.transacao.saldoNovoDestino", is(800.00)));

        verify(beneficioService, times(1)).transfer(any(TransferenciaRequestDTO.class));
    }

    @Test
    @DisplayName("POST /api/beneficios/transferir - Deve retornar 400 com IDs inválidos")
    void testTransfer_ValidationError_IdsNull() throws Exception {
        TransferenciaRequestDTO requestDTO = new TransferenciaRequestDTO();
        requestDTO.setValor(new BigDecimal("300.00"));

        mockMvc.perform(post("/api/beneficios/transferir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)));

        verify(beneficioService, never()).transfer(any());
    }

    @Test
    @DisplayName("POST /api/beneficios/transferir - Deve retornar 400 com valor zero")
    void testTransfer_ValidationError_ValorZero() throws Exception {
        TransferenciaRequestDTO requestDTO = new TransferenciaRequestDTO();
        requestDTO.setBeneficioOrigemId(1L);
        requestDTO.setBeneficioDestinoId(2L);
        requestDTO.setValor(BigDecimal.ZERO);

        mockMvc.perform(post("/api/beneficios/transferir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)));

        verify(beneficioService, never()).transfer(any());
    }

    @Test
    @DisplayName("POST /api/beneficios/transferir - Deve retornar 404 quando origem não existe")
    void testTransfer_OrigemNotFound() throws Exception {
        TransferenciaRequestDTO requestDTO = new TransferenciaRequestDTO();
        requestDTO.setBeneficioOrigemId(999L);
        requestDTO.setBeneficioDestinoId(2L);
        requestDTO.setValor(new BigDecimal("300.00"));

        when(beneficioService.transfer(any(TransferenciaRequestDTO.class)))
                .thenThrow(new BeneficioNotFoundException(999L));

        mockMvc.perform(post("/api/beneficios/transferir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", containsString("999")));

        verify(beneficioService, times(1)).transfer(any(TransferenciaRequestDTO.class));
    }

    @Test
    @DisplayName("POST /api/beneficios/transferir - Deve retornar 409 com saldo insuficiente")
    void testTransfer_SaldoInsuficiente() throws Exception {
        TransferenciaRequestDTO requestDTO = new TransferenciaRequestDTO();
        requestDTO.setBeneficioOrigemId(1L);
        requestDTO.setBeneficioDestinoId(2L);
        requestDTO.setValor(new BigDecimal("2000.00"));

        when(beneficioService.transfer(any(TransferenciaRequestDTO.class)))
                .thenThrow(new BeneficioConflictException("Saldo insuficiente no benefício de origem"));

        mockMvc.perform(post("/api/beneficios/transferir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.error", is("Conflict")))
                .andExpect(jsonPath("$.message", containsString("Saldo insuficiente")));

        verify(beneficioService, times(1)).transfer(any(TransferenciaRequestDTO.class));
    }

    @Test
    @DisplayName("POST /api/beneficios/transferir - Deve retornar 409 quando IDs são iguais")
    void testTransfer_MesmoId() throws Exception {
        TransferenciaRequestDTO requestDTO = new TransferenciaRequestDTO();
        requestDTO.setBeneficioOrigemId(1L);
        requestDTO.setBeneficioDestinoId(1L);
        requestDTO.setValor(new BigDecimal("300.00"));

        when(beneficioService.transfer(any(TransferenciaRequestDTO.class)))
                .thenThrow(new BeneficioConflictException("Benefício de origem e destino não podem ser iguais"));

        mockMvc.perform(post("/api/beneficios/transferir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.message", containsString("iguais")));

        verify(beneficioService, times(1)).transfer(any(TransferenciaRequestDTO.class));
    }
}
