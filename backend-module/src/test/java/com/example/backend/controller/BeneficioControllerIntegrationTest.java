package com.example.backend.controller;

import com.example.backend.dto.BeneficioDTO;
import com.example.backend.dto.BeneficioRequestDTO;
import com.example.backend.dto.ErrorResponse;
import com.example.backend.dto.TransferenciaRequestDTO;
import com.example.backend.dto.TransferenciaResponseDTO;
import com.example.backend.entity.Beneficio;
import com.example.backend.repository.BeneficioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("BeneficioController - Testes de Integração")
class BeneficioControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private BeneficioRepository beneficioRepository;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/beneficios";
        beneficioRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve criar, buscar, atualizar e excluir benefício (ciclo completo)")
    void testCrudCompleto() {
        BeneficioRequestDTO createRequest = new BeneficioRequestDTO();
        createRequest.setNome("Vale Alimentação");
        createRequest.setDescricao("Benefício para alimentação");
        createRequest.setValor(new BigDecimal("800.00"));
        createRequest.setAtivo(true);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<BeneficioRequestDTO> createEntity = new HttpEntity<>(createRequest, headers);

        ResponseEntity<BeneficioDTO> createResponse = restTemplate.postForEntity(
                baseUrl,
                createEntity,
                BeneficioDTO.class
        );

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResponse.getBody()).isNotNull();
        assertThat(createResponse.getBody().getId()).isNotNull();
        assertThat(createResponse.getBody().getNome()).isEqualTo("Vale Alimentação");
        assertThat(createResponse.getBody().getValor()).isEqualByComparingTo(new BigDecimal("800.00"));

        Long beneficioId = createResponse.getBody().getId();

        ResponseEntity<BeneficioDTO> getResponse = restTemplate.getForEntity(
                baseUrl + "/" + beneficioId,
                BeneficioDTO.class
        );

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        assertThat(getResponse.getBody().getId()).isEqualTo(beneficioId);
        assertThat(getResponse.getBody().getNome()).isEqualTo("Vale Alimentação");

        ResponseEntity<List<BeneficioDTO>> listResponse = restTemplate.exchange(
                baseUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<BeneficioDTO>>() {}
        );

        assertThat(listResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(listResponse.getBody()).hasSize(1);
        assertThat(listResponse.getBody().get(0).getNome()).isEqualTo("Vale Alimentação");

        BeneficioRequestDTO updateRequest = new BeneficioRequestDTO();
        updateRequest.setNome("Vale Alimentação Atualizado");
        updateRequest.setDescricao("Descrição atualizada");
        updateRequest.setValor(new BigDecimal("1000.00"));
        updateRequest.setAtivo(true);

        HttpEntity<BeneficioRequestDTO> updateEntity = new HttpEntity<>(updateRequest, headers);
        ResponseEntity<BeneficioDTO> updateResponse = restTemplate.exchange(
                baseUrl + "/" + beneficioId,
                HttpMethod.PUT,
                updateEntity,
                BeneficioDTO.class
        );

        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResponse.getBody()).isNotNull();
        assertThat(updateResponse.getBody().getNome()).isEqualTo("Vale Alimentação Atualizado");
        assertThat(updateResponse.getBody().getValor()).isEqualByComparingTo(new BigDecimal("1000.00"));

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                baseUrl + "/" + beneficioId,
                HttpMethod.DELETE,
                null,
                Void.class
        );

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        Beneficio beneficioDeletado = beneficioRepository.findById(beneficioId).orElse(null);
        assertThat(beneficioDeletado).isNotNull();
        assertThat(beneficioDeletado.getAtivo()).isFalse();

        ResponseEntity<BeneficioDTO> getAfterDeleteResponse = restTemplate.getForEntity(
                baseUrl + "/" + beneficioId,
                BeneficioDTO.class
        );

        assertThat(getAfterDeleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getAfterDeleteResponse.getBody()).isNotNull();
        assertThat(getAfterDeleteResponse.getBody().getAtivo()).isFalse();
    }

    @Test
    @DisplayName("Deve realizar transferência entre dois benefícios com sucesso")
    void testTransferenciaSuccess() {
        // Criar benefício origem
        Beneficio origem = new Beneficio();
        origem.setNome("Benefício Origem");
        origem.setDescricao("Origem para transferência");
        origem.setValor(new BigDecimal("1000.00"));
        origem.setAtivo(true);
        origem = beneficioRepository.save(origem);

        Beneficio destino = new Beneficio();
        destino.setNome("Benefício Destino");
        destino.setDescricao("Destino para transferência");
        destino.setValor(new BigDecimal("500.00"));
        destino.setAtivo(true);
        destino = beneficioRepository.save(destino);

        TransferenciaRequestDTO transferenciaRequest = new TransferenciaRequestDTO();
        transferenciaRequest.setBeneficioOrigemId(origem.getId());
        transferenciaRequest.setBeneficioDestinoId(destino.getId());
        transferenciaRequest.setValor(new BigDecimal("300.00"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TransferenciaRequestDTO> entity = new HttpEntity<>(transferenciaRequest, headers);

        ResponseEntity<TransferenciaResponseDTO> response = restTemplate.postForEntity(
                baseUrl + "/transferir",
                entity,
                TransferenciaResponseDTO.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getSucesso()).isTrue();
        assertThat(response.getBody().getMensagem()).contains("sucesso");
        
        TransferenciaResponseDTO.TransacaoDTO transacao = response.getBody().getTransacao();
        assertThat(transacao).isNotNull();
        assertThat(transacao.getBeneficioOrigemId()).isEqualTo(origem.getId());
        assertThat(transacao.getBeneficioDestinoId()).isEqualTo(destino.getId());
        assertThat(transacao.getValorTransferido()).isEqualByComparingTo(new BigDecimal("300.00"));
        assertThat(transacao.getSaldoAnteriorOrigem()).isEqualByComparingTo(new BigDecimal("1000.00"));
        assertThat(transacao.getSaldoNovoOrigem()).isEqualByComparingTo(new BigDecimal("700.00"));
        assertThat(transacao.getSaldoAnteriorDestino()).isEqualByComparingTo(new BigDecimal("500.00"));
        assertThat(transacao.getSaldoNovoDestino()).isEqualByComparingTo(new BigDecimal("800.00"));

        // Validar no banco de dados
        Beneficio origemAtualizado = beneficioRepository.findById(origem.getId()).orElseThrow();
        Beneficio destinoAtualizado = beneficioRepository.findById(destino.getId()).orElseThrow();

        assertThat(origemAtualizado.getValor()).isEqualByComparingTo(new BigDecimal("700.00"));
        assertThat(destinoAtualizado.getValor()).isEqualByComparingTo(new BigDecimal("800.00"));
    }

    @Test
    @DisplayName("Deve retornar 409 ao tentar transferir com saldo insuficiente")
    void testTransferenciaSaldoInsuficiente() {
        Beneficio origem = new Beneficio();
        origem.setNome("Origem Saldo Baixo");
        origem.setDescricao("Benefício com saldo insuficiente");
        origem.setValor(new BigDecimal("100.00"));
        origem.setAtivo(true);
        origem = beneficioRepository.save(origem);

        Beneficio destino = new Beneficio();
        destino.setNome("Destino");
        destino.setDescricao("Benefício destino");
        destino.setValor(new BigDecimal("500.00"));
        destino.setAtivo(true);
        destino = beneficioRepository.save(destino);

        TransferenciaRequestDTO transferenciaRequest = new TransferenciaRequestDTO();
        transferenciaRequest.setBeneficioOrigemId(origem.getId());
        transferenciaRequest.setBeneficioDestinoId(destino.getId());
        transferenciaRequest.setValor(new BigDecimal("200.00"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TransferenciaRequestDTO> entity = new HttpEntity<>(transferenciaRequest, headers);

        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
                baseUrl + "/transferir",
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(409);
        assertThat(response.getBody().getMessage()).containsIgnoringCase("saldo insuficiente");

        Beneficio origemNaoAlterado = beneficioRepository.findById(origem.getId()).orElseThrow();
        Beneficio destinoNaoAlterado = beneficioRepository.findById(destino.getId()).orElseThrow();

        assertThat(origemNaoAlterado.getValor()).isEqualByComparingTo(new BigDecimal("100.00"));
        assertThat(destinoNaoAlterado.getValor()).isEqualByComparingTo(new BigDecimal("500.00"));
    }

    @Test
    @DisplayName("Deve retornar 409 ao tentar criar benefício com nome duplicado")
    void testCriarBeneficioNomeDuplicado() {
        Beneficio existente = new Beneficio();
        existente.setNome("Vale Transporte");
        existente.setDescricao("Primeiro benefício");
        existente.setValor(new BigDecimal("500.00"));
        existente.setAtivo(true);
        beneficioRepository.save(existente);

        BeneficioRequestDTO request = new BeneficioRequestDTO();
        request.setNome("Vale Transporte");
        request.setDescricao("Tentativa de duplicação");
        request.setValor(new BigDecimal("600.00"));
        request.setAtivo(true);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<BeneficioRequestDTO> entity = new HttpEntity<>(request, headers);

        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
                baseUrl,
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(409);
        assertThat(response.getBody().getMessage()).containsIgnoringCase("Vale Transporte");
    }

    @Test
    @DisplayName("Deve retornar 400 ao tentar criar benefício com dados inválidos")
    void testCriarBeneficioValidacaoFalha() {
        BeneficioRequestDTO request = new BeneficioRequestDTO();
        request.setNome("AB");
        request.setDescricao("Descrição válida");
        request.setValor(new BigDecimal("500.00"));
        request.setAtivo(true);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<BeneficioRequestDTO> entity = new HttpEntity<>(request, headers);

        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
                baseUrl,
                entity,
                ErrorResponse.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getErrors()).isNotEmpty();
        assertThat(response.getBody().getErrors().get(0).getField()).isEqualTo("nome");
    }

    @Test
    @DisplayName("Deve retornar 404 ao buscar benefício inexistente")
    void testBuscarBeneficioInexistente() {
        ResponseEntity<ErrorResponse> response = restTemplate.getForEntity(
                baseUrl + "/999",
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getMessage()).containsIgnoringCase("999");
    }

    @Test
    @DisplayName("Deve retornar 409 ao tentar transferir entre o mesmo benefício")
    void testTransferenciaMesmoBeneficio() {
        Beneficio beneficio = new Beneficio();
        beneficio.setNome("Benefício Único");
        beneficio.setDescricao("Para teste de transferência inválida");
        beneficio.setValor(new BigDecimal("1000.00"));
        beneficio.setAtivo(true);
        beneficio = beneficioRepository.save(beneficio);

        TransferenciaRequestDTO request = new TransferenciaRequestDTO();
        request.setBeneficioOrigemId(beneficio.getId());
        request.setBeneficioDestinoId(beneficio.getId());
        request.setValor(new BigDecimal("100.00"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TransferenciaRequestDTO> entity = new HttpEntity<>(request, headers);

        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
                baseUrl + "/transferir",
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(409);
        assertThat(response.getBody().getMessage()).containsIgnoringCase("iguais");
    }

    @Test
    @DisplayName("Deve retornar 409 ao tentar transferir de benefício inativo")
    void testTransferenciaBeneficioInativo() {
        Beneficio origem = new Beneficio();
        origem.setNome("Origem Inativa");
        origem.setDescricao("Benefício inativo");
        origem.setValor(new BigDecimal("1000.00"));
        origem.setAtivo(false);
        origem = beneficioRepository.save(origem);

        Beneficio destino = new Beneficio();
        destino.setNome("Destino Ativo");
        destino.setDescricao("Benefício ativo");
        destino.setValor(new BigDecimal("500.00"));
        destino.setAtivo(true);
        destino = beneficioRepository.save(destino);

        TransferenciaRequestDTO request = new TransferenciaRequestDTO();
        request.setBeneficioOrigemId(origem.getId());
        request.setBeneficioDestinoId(destino.getId());
        request.setValor(new BigDecimal("100.00"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TransferenciaRequestDTO> entity = new HttpEntity<>(request, headers);

        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
                baseUrl + "/transferir",
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(409);
        assertThat(response.getBody().getMessage()).containsIgnoringCase("inativo");
    }

    @Test
    @DisplayName("Deve listar apenas benefícios ativos")
    void testListarApenasBeneficiosAtivos() {
        Beneficio ativo1 = new Beneficio();
        ativo1.setNome("Ativo 1");
        ativo1.setDescricao("Primeiro ativo");
        ativo1.setValor(new BigDecimal("500.00"));
        ativo1.setAtivo(true);
        beneficioRepository.save(ativo1);

        Beneficio ativo2 = new Beneficio();
        ativo2.setNome("Ativo 2");
        ativo2.setDescricao("Segundo ativo");
        ativo2.setValor(new BigDecimal("600.00"));
        ativo2.setAtivo(true);
        beneficioRepository.save(ativo2);

        Beneficio inativo = new Beneficio();
        inativo.setNome("Inativo");
        inativo.setDescricao("Benefício inativo");
        inativo.setValor(new BigDecimal("700.00"));
        inativo.setAtivo(false);
        beneficioRepository.save(inativo);

        ResponseEntity<List<BeneficioDTO>> response = restTemplate.exchange(
                baseUrl + "/ativos",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<BeneficioDTO>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody())
                .extracting(BeneficioDTO::getNome)
                .containsExactlyInAnyOrder("Ativo 1", "Ativo 2");
    }

    @Test
    @DisplayName("GET /api/beneficios/ativos - Deve retornar apenas benefícios ativos")
    void testFindAllAtivos_DeveRetornarApenasAtivos() {
        Beneficio ativo1 = new Beneficio();
        ativo1.setNome("Vale Refeição");
        ativo1.setDescricao("Ativo 1");
        ativo1.setValor(new BigDecimal("500.00"));
        ativo1.setAtivo(true);
        beneficioRepository.save(ativo1);

        Beneficio ativo2 = new Beneficio();
        ativo2.setNome("Vale Transporte");
        ativo2.setDescricao("Ativo 2");
        ativo2.setValor(new BigDecimal("300.00"));
        ativo2.setAtivo(true);
        beneficioRepository.save(ativo2);

        Beneficio ativo3 = new Beneficio();
        ativo3.setNome("Vale Cultura");
        ativo3.setDescricao("Ativo 3");
        ativo3.setValor(new BigDecimal("200.00"));
        ativo3.setAtivo(true);
        beneficioRepository.save(ativo3);

        Beneficio inativo1 = new Beneficio();
        inativo1.setNome("Benefício Inativo 1");
        inativo1.setDescricao("Inativo 1");
        inativo1.setValor(new BigDecimal("100.00"));
        inativo1.setAtivo(false);
        beneficioRepository.save(inativo1);

        Beneficio inativo2 = new Beneficio();
        inativo2.setNome("Benefício Inativo 2");
        inativo2.setDescricao("Inativo 2");
        inativo2.setValor(new BigDecimal("150.00"));
        inativo2.setAtivo(false);
        beneficioRepository.save(inativo2);

        ResponseEntity<List<BeneficioDTO>> todosResponse = restTemplate.exchange(
                baseUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<BeneficioDTO>>() {}
        );

        assertThat(todosResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(todosResponse.getBody()).hasSize(5);

        ResponseEntity<List<BeneficioDTO>> ativosResponse = restTemplate.exchange(
                baseUrl + "/ativos",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<BeneficioDTO>>() {}
        );

        assertThat(ativosResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(ativosResponse.getBody()).hasSize(3);
        assertThat(ativosResponse.getBody())
                .extracting(BeneficioDTO::getAtivo)
                .containsOnly(true);
        assertThat(ativosResponse.getBody())
                .extracting(BeneficioDTO::getNome)
                .containsExactlyInAnyOrder("Vale Refeição", "Vale Transporte", "Vale Cultura");
    }

    @Test
    @DisplayName("GET /api/beneficios/ativos - Deve retornar lista vazia quando não há ativos")
    void testFindAllAtivos_ListaVazia() {
        Beneficio inativo1 = new Beneficio();
        inativo1.setNome("Inativo 1");
        inativo1.setDescricao("Benefício inativo");
        inativo1.setValor(new BigDecimal("100.00"));
        inativo1.setAtivo(false);
        beneficioRepository.save(inativo1);

        ResponseEntity<List<BeneficioDTO>> response = restTemplate.exchange(
                baseUrl + "/ativos",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<BeneficioDTO>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    @DisplayName("Deve impedir transferência de/para benefício inativo")
    void testTransferenciaComBeneficioInativo() {
        Beneficio ativo = new Beneficio();
        ativo.setNome("Benefício Ativo");
        ativo.setDescricao("Ativo");
        ativo.setValor(new BigDecimal("1000.00"));
        ativo.setAtivo(true);
        ativo = beneficioRepository.save(ativo);

        Beneficio inativo = new Beneficio();
        inativo.setNome("Benefício Inativo");
        inativo.setDescricao("Inativo");
        inativo.setValor(new BigDecimal("500.00"));
        inativo.setAtivo(false);
        inativo = beneficioRepository.save(inativo);

        TransferenciaRequestDTO request1 = new TransferenciaRequestDTO();
        request1.setBeneficioOrigemId(inativo.getId());
        request1.setBeneficioDestinoId(ativo.getId());
        request1.setValor(new BigDecimal("100.00"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TransferenciaRequestDTO> entity1 = new HttpEntity<>(request1, headers);

        ResponseEntity<ErrorResponse> response1 = restTemplate.postForEntity(
                baseUrl + "/transferir",
                entity1,
                ErrorResponse.class
        );

        assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response1.getBody()).isNotNull();
        assertThat(response1.getBody().getMessage()).containsIgnoringCase("inativo");

        TransferenciaRequestDTO request2 = new TransferenciaRequestDTO();
        request2.setBeneficioOrigemId(ativo.getId());
        request2.setBeneficioDestinoId(inativo.getId());
        request2.setValor(new BigDecimal("100.00"));

        HttpEntity<TransferenciaRequestDTO> entity2 = new HttpEntity<>(request2, headers);

        ResponseEntity<ErrorResponse> response2 = restTemplate.postForEntity(
                baseUrl + "/transferir",
                entity2,
                ErrorResponse.class
        );

        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response2.getBody()).isNotNull();
        assertThat(response2.getBody().getMessage()).containsIgnoringCase("inativo");
    }

    @Test
    @DisplayName("Deve validar valores extremos ao criar benefício")
    void testCriarBeneficioComValoresExtremos() {
        BeneficioRequestDTO requestValorGrande = new BeneficioRequestDTO();
        requestValorGrande.setNome("Benefício Grande");
        requestValorGrande.setDescricao("Teste de valor grande");
        requestValorGrande.setValor(new BigDecimal("99999999.99"));
        requestValorGrande.setAtivo(true);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<BeneficioRequestDTO> entityGrande = new HttpEntity<>(requestValorGrande, headers);

        ResponseEntity<BeneficioDTO> responseGrande = restTemplate.postForEntity(
                baseUrl,
                entityGrande,
                BeneficioDTO.class
        );

        assertThat(responseGrande.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseGrande.getBody()).isNotNull();
        assertThat(responseGrande.getBody().getValor()).isEqualByComparingTo(new BigDecimal("99999999.99"));

        BeneficioRequestDTO requestValorMinimo = new BeneficioRequestDTO();
        requestValorMinimo.setNome("Benefício Mínimo");
        requestValorMinimo.setDescricao("Teste de valor mínimo");
        requestValorMinimo.setValor(new BigDecimal("0.01"));
        requestValorMinimo.setAtivo(true);

        HttpEntity<BeneficioRequestDTO> entityMinimo = new HttpEntity<>(requestValorMinimo, headers);

        ResponseEntity<BeneficioDTO> responseMinimo = restTemplate.postForEntity(
                baseUrl,
                entityMinimo,
                BeneficioDTO.class
        );

        assertThat(responseMinimo.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseMinimo.getBody()).isNotNull();
        assertThat(responseMinimo.getBody().getValor()).isEqualByComparingTo(new BigDecimal("0.01"));

        String nomeLongo = "A".repeat(100);
        BeneficioRequestDTO requestNomeLongo = new BeneficioRequestDTO();
        requestNomeLongo.setNome(nomeLongo);
        requestNomeLongo.setDescricao("Teste de nome longo");
        requestNomeLongo.setValor(new BigDecimal("500.00"));
        requestNomeLongo.setAtivo(true);

        HttpEntity<BeneficioRequestDTO> entityNomeLongo = new HttpEntity<>(requestNomeLongo, headers);

        ResponseEntity<BeneficioDTO> responseNomeLongo = restTemplate.postForEntity(
                baseUrl,
                entityNomeLongo,
                BeneficioDTO.class
        );

        assertThat(responseNomeLongo.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseNomeLongo.getBody()).isNotNull();
        assertThat(responseNomeLongo.getBody().getNome()).hasSize(100);
    }

    @Test
    @DisplayName("Deve validar rollback em caso de erro na transferência")
    void testTransferenciaRollbackEmCasoDeErro() {
        Beneficio origem = new Beneficio();
        origem.setNome("Origem");
        origem.setDescricao("Benefício origem");
        origem.setValor(new BigDecimal("1000.00"));
        origem.setAtivo(true);
        origem = beneficioRepository.save(origem);

        BigDecimal saldoOriginalOrigem = origem.getValor();

        TransferenciaRequestDTO request = new TransferenciaRequestDTO();
        request.setBeneficioOrigemId(origem.getId());
        request.setBeneficioDestinoId(99999L); // ID inexistente
        request.setValor(new BigDecimal("200.00"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TransferenciaRequestDTO> entity = new HttpEntity<>(request, headers);

        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
                baseUrl + "/transferir",
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        Beneficio origemAposErro = beneficioRepository.findById(origem.getId()).orElseThrow();
        assertThat(origemAposErro.getValor()).isEqualByComparingTo(saldoOriginalOrigem);
    }

    @Test
    @DisplayName("Deve permitir atualizar benefício exceto se nome ficar duplicado")
    void testAtualizarBeneficioNomeDuplicado() {
        Beneficio beneficio1 = new Beneficio();
        beneficio1.setNome("Benefício 1");
        beneficio1.setDescricao("Primeiro");
        beneficio1.setValor(new BigDecimal("500.00"));
        beneficio1.setAtivo(true);
        beneficio1 = beneficioRepository.save(beneficio1);

        Beneficio beneficio2 = new Beneficio();
        beneficio2.setNome("Benefício 2");
        beneficio2.setDescricao("Segundo");
        beneficio2.setValor(new BigDecimal("600.00"));
        beneficio2.setAtivo(true);
        beneficio2 = beneficioRepository.save(beneficio2);

        BeneficioRequestDTO request = new BeneficioRequestDTO();
        request.setNome("Benefício 1");
        request.setDescricao("Tentativa de duplicação");
        request.setValor(new BigDecimal("700.00"));
        request.setAtivo(true);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<BeneficioRequestDTO> entity = new HttpEntity<>(request, headers);

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                baseUrl + "/" + beneficio2.getId(),
                HttpMethod.PUT,
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(409);
        assertThat(response.getBody().getMessage()).containsIgnoringCase("Benefício 1");
    }
}
