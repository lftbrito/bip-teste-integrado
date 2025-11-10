package com.example.backend.service;

import com.example.backend.dto.*;
import com.example.backend.entity.Beneficio;
import com.example.backend.exception.BeneficioConflictException;
import com.example.backend.exception.BeneficioNotFoundException;
import com.example.backend.mapper.BeneficioMapper;
import com.example.backend.repository.BeneficioRepository;
import jakarta.persistence.OptimisticLockException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service para operações de negócio relacionadas a Benefícios.
 * 
 * <p>Implementa lógica de CRUD e integração com EJB service
 * para operações transacionais complexas como transferências.</p>
 * 
 * @author Sistema
 * @version 1.0.0
 * @since 2025-11-08
 */
@Service
@Transactional
public class BeneficioService {

    private static final Logger log = LoggerFactory.getLogger(BeneficioService.class);
    private static final int MAX_RETRY_ATTEMPTS = 10;
    private static final long RETRY_DELAY_MS = 100;

    private final BeneficioRepository repository;
    private final BeneficioMapper mapper;

    public BeneficioService(BeneficioRepository repository, BeneficioMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * Lista todos os benefícios (ativos e inativos).
     * 
     * @return lista de DTOs com todos os benefícios
     */
    @Transactional(readOnly = true)
    public List<BeneficioDTO> findAll() {
        log.debug("Buscando todos os benefícios");
        return repository.findAll()
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lista apenas os benefícios ativos.
     * 
     * @return lista de DTOs com benefícios ativos
     */
    @Transactional(readOnly = true)
    public List<BeneficioDTO> findAllAtivos() {
        log.debug("Buscando benefícios ativos");
        return repository.findAllAtivos()
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca um benefício por ID.
     * 
     * @param id ID do benefício
     * @return DTO do benefício encontrado
     * @throws BeneficioNotFoundException se o benefício não existir
     */
    @Transactional(readOnly = true)
    public BeneficioDTO findById(Long id) {
        log.debug("Buscando benefício com ID: {}", id);
        Beneficio entity = repository.findById(id)
                .orElseThrow(() -> new BeneficioNotFoundException(id));
        return mapper.toDTO(entity);
    }

    /**
     * Cria um novo benefício.
     * 
     * @param requestDTO dados do novo benefício
     * @return DTO do benefício criado
     */
    public BeneficioDTO create(BeneficioRequestDTO requestDTO) {
        log.info("Criando novo benefício: {}", requestDTO.getNome());
        
        // Validação: verificar se já existe benefício com mesmo nome
        if (repository.existsByNome(requestDTO.getNome())) {
            log.warn("Tentativa de criar benefício com nome duplicado: {}", requestDTO.getNome());
            throw new BeneficioConflictException("Já existe um benefício com o nome: " + requestDTO.getNome());
        }

        Beneficio entity = mapper.toEntity(requestDTO);
        Beneficio saved = repository.save(entity);
        
        log.info("Benefício criado com sucesso. ID: {}", saved.getId());
        return mapper.toDTO(saved);
    }

    /**
     * Atualiza um benefício existente.
     * 
     * @param id ID do benefício a atualizar
     * @param requestDTO novos dados do benefício
     * @return DTO do benefício atualizado
     * @throws BeneficioNotFoundException se o benefício não existir
     */
    public BeneficioDTO update(Long id, BeneficioRequestDTO requestDTO) {
        log.info("Atualizando benefício ID: {}", id);
        
        Beneficio entity = repository.findById(id)
                .orElseThrow(() -> new BeneficioNotFoundException(id));

        // Validação: verificar se o novo nome já existe em outro benefício
        if (repository.existsByNomeAndIdNot(requestDTO.getNome(), id)) {
            log.warn("Tentativa de atualizar benefício com nome duplicado: {}", requestDTO.getNome());
            throw new BeneficioConflictException("Já existe outro benefício com o nome: " + requestDTO.getNome());
        }

        mapper.updateEntity(entity, requestDTO);
        Beneficio updated = repository.save(entity);
        
        log.info("Benefício atualizado com sucesso. ID: {}", id);
        return mapper.toDTO(updated);
    }

    /**
     * Exclui (soft delete) um benefício.
     * 
     * @param id ID do benefício a excluir
     * @throws BeneficioNotFoundException se o benefício não existir
     */
    public void delete(Long id) {
        log.info("Excluindo benefício ID: {}", id);
        
        Beneficio entity = repository.findById(id)
                .orElseThrow(() -> new BeneficioNotFoundException(id));

        // Soft delete: apenas marca como inativo
        entity.setAtivo(false);
        repository.save(entity);
        
        log.info("Benefício excluído (soft delete) com sucesso. ID: {}", id);
    }

    /**
     * Realiza transferência de valor entre benefícios com optimistic locking.
     * 
     * <p>Implementa retry logic para lidar com conflitos de concorrência.
     * Utiliza o campo @Version da entidade para controle de versão.</p>
     * 
     * @param requestDTO dados da transferência
     * @return resposta com detalhes da transferência
     * @throws BeneficioNotFoundException se origem ou destino não existir
     * @throws BeneficioConflictException se validação falhar ou retry esgotar
     */
    public TransferenciaResponseDTO transfer(TransferenciaRequestDTO requestDTO) {
        log.info("Iniciando transferência: {} -> {}, valor: {}", 
                requestDTO.getBeneficioOrigemId(), 
                requestDTO.getBeneficioDestinoId(), 
                requestDTO.getValor());

        // Validações básicas
        if (requestDTO.getBeneficioOrigemId().equals(requestDTO.getBeneficioDestinoId())) {
            throw new BeneficioConflictException("Origem e destino não podem ser iguais");
        }

        if (requestDTO.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BeneficioConflictException("Valor deve ser maior que zero");
        }

        // Implementar retry logic para optimistic locking
        int attempts = 0;
        Exception lastException = null;
        
        while (attempts < MAX_RETRY_ATTEMPTS) {
            try {
                return executeTransfer(requestDTO);
            } catch (ObjectOptimisticLockingFailureException | OptimisticLockException e) {
                lastException = e;
                attempts++;
                log.warn("Conflito de concorrência detectado. Tentativa {}/{}", attempts, MAX_RETRY_ATTEMPTS);
                
                if (attempts >= MAX_RETRY_ATTEMPTS) {
                    log.error("Número máximo de tentativas excedido para transferência");
                    throw new BeneficioConflictException(
                        "Transferência falhou devido a conflito de concorrência. Tente novamente.");
                }
                
                // Aguardar antes de tentar novamente com backoff exponencial
                try {
                    Thread.sleep(RETRY_DELAY_MS * attempts);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new BeneficioConflictException("Transferência interrompida");
                }
            }
        }
        
        throw new BeneficioConflictException("Falha ao processar transferência");
    }

    /**
     * Executa a transferência efetivamente dentro de uma transação.
     * Método separado para facilitar retry logic.
     */
    @Transactional
    private TransferenciaResponseDTO executeTransfer(TransferenciaRequestDTO requestDTO) {
        // Buscar benefícios
        Beneficio origem = repository.findById(requestDTO.getBeneficioOrigemId())
                .orElseThrow(() -> new BeneficioNotFoundException("Benefício de origem não encontrado: " 
                        + requestDTO.getBeneficioOrigemId()));

        Beneficio destino = repository.findById(requestDTO.getBeneficioDestinoId())
                .orElseThrow(() -> new BeneficioNotFoundException("Benefício de destino não encontrado: " 
                        + requestDTO.getBeneficioDestinoId()));

        // Validar se ambos estão ativos
        if (!origem.getAtivo()) {
            throw new BeneficioConflictException("Benefício de origem está inativo");
        }
        if (!destino.getAtivo()) {
            throw new BeneficioConflictException("Benefício de destino está inativo");
        }

        // Validar saldo suficiente
        if (origem.getValor().compareTo(requestDTO.getValor()) < 0) {
            throw new BeneficioConflictException(
                    String.format("Saldo insuficiente. Disponível: %.2f, Solicitado: %.2f",
                            origem.getValor(), requestDTO.getValor()));
        }

        // Capturar saldos anteriores
        BigDecimal saldoAnteriorOrigem = origem.getValor();
        BigDecimal saldoAnteriorDestino = destino.getValor();

        // Realizar transferência
        origem.setValor(origem.getValor().subtract(requestDTO.getValor()));
        destino.setValor(destino.getValor().add(requestDTO.getValor()));

        // Salvar com optimistic locking - @Version será verificado
        repository.save(origem);
        repository.save(destino);

        log.info("Transferência concluída com sucesso. From: {} -> {}, To: {} -> {}",
                saldoAnteriorOrigem, origem.getValor(),
                saldoAnteriorDestino, destino.getValor());

        // Construir resposta
        TransferenciaResponseDTO.TransacaoDTO transacao = new TransferenciaResponseDTO.TransacaoDTO(
                requestDTO.getBeneficioOrigemId(),
                requestDTO.getBeneficioDestinoId(),
                requestDTO.getValor(),
                saldoAnteriorOrigem,
                origem.getValor(),
                saldoAnteriorDestino,
                destino.getValor()
        );

        return new TransferenciaResponseDTO(
                true,
                "Transferência realizada com sucesso",
                transacao
        );
    }
}
