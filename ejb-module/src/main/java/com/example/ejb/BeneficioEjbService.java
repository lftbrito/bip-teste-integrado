package com.example.ejb;

import com.example.ejb.entity.Beneficio;
import com.example.ejb.exception.*;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

/**
 * Serviço EJB para operações transacionais de Benefícios.
 * 
 * <p>Este serviço implementa operações críticas que requerem garantias ACID,
 * especialmente a transferência de valores entre benefícios com:
 * <ul>
 *   <li>Atomicidade: Ou ambas operações ocorrem ou nenhuma</li>
 *   <li>Consistência: Validações impedem estados inválidos</li>
 *   <li>Isolamento: Pessimistic locking previne race conditions</li>
 *   <li>Durabilidade: Transação JTA garante persistência</li>
 * </ul>
 * 
 * <p><b>CORREÇÕES IMPLEMENTADAS:</b>
 * <ul>
 *   <li>✅ Validação de saldo suficiente</li>
 *   <li>✅ Validação de benefícios ativos</li>
 *   <li>✅ Pessimistic locking para prevenir lost updates</li>
 *   <li>✅ Lock ordering para prevenir deadlocks</li>
 *   <li>✅ Validações de entrada completas</li>
 *   <li>✅ Logging detalhado para auditoria</li>
 *   <li>✅ Exceptions específicas com rollback automático</li>
 * </ul>
 * 
 * @author Sistema de Benefícios
 * @version 2.0 - Corrigido
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class BeneficioEjbService {
    
    private static final Logger logger = LoggerFactory.getLogger(BeneficioEjbService.class);
    
    @PersistenceContext(unitName = "beneficioPU")
    private EntityManager em;
    
    /**
     * Transfere valor entre dois benefícios com garantias de atomicidade e consistência.
     * 
     * <p>Esta implementação corrige os seguintes bugs da versão anterior:
     * <ul>
     *   <li>Bug #1: Permite saldo negativo → CORRIGIDO com validação de saldo</li>
     *   <li>Bug #2: NullPointerException → CORRIGIDO com validação de existência</li>
     *   <li>Bug #3: Race condition (lost update) → CORRIGIDO com pessimistic locking</li>
     *   <li>Bug #4: Sem tratamento de exceções → CORRIGIDO com exceptions específicas</li>
     *   <li>Bug #5: Sem validações de negócio → CORRIGIDO com validações completas</li>
     * </ul>
     * 
     * <p><b>Estratégia de Locking:</b><br>
     * Usa pessimistic write lock na ordem crescente de IDs para:
     * <ul>
     *   <li>Prevenir lost updates (dois threads modificando a mesma entity)</li>
     *   <li>Prevenir deadlocks (sempre adquire locks na mesma ordem)</li>
     *   <li>Garantir serialização de acessos concorrentes</li>
     * </ul>
     * 
     * @param fromId ID do benefício origem (débito)
     * @param toId ID do benefício destino (crédito)
     * @param amount Valor a transferir (deve ser positivo)
     * @throws BeneficioException se houver qualquer violação de regra de negócio
     */
    public void transfer(Long fromId, Long toId, BigDecimal amount) 
            throws BeneficioException {
        
        logger.info("Iniciando transferência: {} -> {} valor: {}", fromId, toId, amount);
        
        // 1. Validações de entrada
        validateTransferInputs(fromId, toId, amount);
        
        // 2. Adquire locks na ordem crescente de IDs (previne deadlock)
        Long firstId = Math.min(fromId, toId);
        Long secondId = Math.max(fromId, toId);
        
        logger.debug("Adquirindo locks: primeiro={}, segundo={}", firstId, secondId);
        
        Beneficio first = em.find(Beneficio.class, firstId, LockModeType.PESSIMISTIC_WRITE);
        Beneficio second = em.find(Beneficio.class, secondId, LockModeType.PESSIMISTIC_WRITE);
        
        // 3. Validações de existência
        if (first == null) {
            logger.warn("Benefício não encontrado: {}", firstId);
            throw new BeneficioNaoEncontradoException(firstId);
        }
        if (second == null) {
            logger.warn("Benefício não encontrado: {}", secondId);
            throw new BeneficioNaoEncontradoException(secondId);
        }
        
        // 4. Identifica origem e destino
        Beneficio from = fromId.equals(firstId) ? first : second;
        Beneficio to = fromId.equals(firstId) ? second : first;
        
        logger.debug("Benefício origem: {}", from);
        logger.debug("Benefício destino: {}", to);
        
        // 5. Validações de negócio
        validateBeneficioAtivo(from, "origem", fromId);
        validateBeneficioAtivo(to, "destino", toId);
        validateSaldoSuficiente(from, amount);
        
        // 6. Executa transferência
        BigDecimal saldoAntigoFrom = from.getValor();
        BigDecimal saldoAntigoTo = to.getValor();
        
        BigDecimal novoSaldoFrom = saldoAntigoFrom.subtract(amount);
        BigDecimal novoSaldoTo = saldoAntigoTo.add(amount);
        
        from.setValor(novoSaldoFrom);
        to.setValor(novoSaldoTo);
        
        em.merge(from);
        em.merge(to);
        
        logger.info("Transferência concluída com sucesso. From: {} -> {}, To: {} -> {}", 
            saldoAntigoFrom, novoSaldoFrom, saldoAntigoTo, novoSaldoTo);
    }
    
    /**
     * Valida os parâmetros de entrada da transferência.
     * 
     * @param fromId ID origem
     * @param toId ID destino
     * @param amount Valor
     * @throws IllegalArgumentException se algum parâmetro for inválido
     */
    private void validateTransferInputs(Long fromId, Long toId, BigDecimal amount) {
        if (fromId == null) {
            throw new IllegalArgumentException("ID do benefício origem não pode ser nulo");
        }
        if (toId == null) {
            throw new IllegalArgumentException("ID do benefício destino não pode ser nulo");
        }
        if (amount == null) {
            throw new IllegalArgumentException("Valor da transferência não pode ser nulo");
        }
        
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                "Valor da transferência deve ser positivo: " + amount);
        }
        
        if (fromId.equals(toId)) {
            throw new IllegalArgumentException(
                "Benefício origem e destino não podem ser iguais: " + fromId);
        }
    }
    
    /**
     * Valida se o benefício está ativo.
     * 
     * @param beneficio Benefício a validar
     * @param tipo "origem" ou "destino" (para mensagem de erro)
     * @param id ID do benefício (para mensagem de erro)
     * @throws BeneficioInativoException se benefício estiver inativo
     */
    private void validateBeneficioAtivo(Beneficio beneficio, String tipo, Long id) 
            throws BeneficioInativoException {
        
        if (!beneficio.getAtivo()) {
            logger.warn("Benefício {} está inativo: {}", tipo, id);
            throw new BeneficioInativoException(id, tipo);
        }
    }
    
    /**
     * Valida se há saldo suficiente para a transferência.
     * 
     * @param from Benefício origem
     * @param amount Valor a transferir
     * @throws SaldoInsuficienteException se saldo for insuficiente
     */
    private void validateSaldoSuficiente(Beneficio from, BigDecimal amount) 
            throws SaldoInsuficienteException {
        
        if (from.getValor().compareTo(amount) < 0) {
            logger.warn("Saldo insuficiente. Disponível: {}, Solicitado: {}", 
                from.getValor(), amount);
            throw new SaldoInsuficienteException(from.getValor(), amount);
        }
    }
}
