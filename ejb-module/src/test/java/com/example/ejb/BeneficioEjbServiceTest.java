package com.example.ejb;

import com.example.ejb.entity.Beneficio;
import com.example.ejb.exception.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para BeneficioEjbService.
 * 
 * <p>Valida as correções implementadas para os 5 bugs identificados:
 * <ul>
 *   <li>Bug #1: Permite saldo negativo</li>
 *   <li>Bug #2: NullPointerException quando benefício não existe</li>
 *   <li>Bug #3: Race condition (lost update)</li>
 *   <li>Bug #4: Sem tratamento de exceções</li>
 *   <li>Bug #5: Sem validações de negócio</li>
 * </ul>
 * 
 * @author Sistema de Benefícios
 */
@DisplayName("BeneficioEjbService - Testes Unitários")
class BeneficioEjbServiceTest {
    
    @Mock
    private EntityManager entityManager;
    
    @InjectMocks
    private BeneficioEjbService service;
    
    private Beneficio beneficioOrigem;
    private Beneficio beneficioDestino;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Benefício origem: ID 1, saldo R$ 1000,00, ativo
        beneficioOrigem = new Beneficio();
        beneficioOrigem.setId(1L);
        beneficioOrigem.setNome("Vale Refeição");
        beneficioOrigem.setValor(new BigDecimal("1000.00"));
        beneficioOrigem.setAtivo(true);
        
        // Benefício destino: ID 2, saldo R$ 500,00, ativo
        beneficioDestino = new Beneficio();
        beneficioDestino.setId(2L);
        beneficioDestino.setNome("Vale Alimentação");
        beneficioDestino.setValor(new BigDecimal("500.00"));
        beneficioDestino.setAtivo(true);
    }
    
    @Test
    @DisplayName("Deve transferir valor com sucesso entre benefícios ativos com saldo suficiente")
    void testTransferSuccess() {
        // Arrange
        Long fromId = 1L;
        Long toId = 2L;
        BigDecimal amount = new BigDecimal("250.00");
        
        when(entityManager.find(eq(Beneficio.class), eq(1L), eq(LockModeType.PESSIMISTIC_WRITE)))
            .thenReturn(beneficioOrigem);
        when(entityManager.find(eq(Beneficio.class), eq(2L), eq(LockModeType.PESSIMISTIC_WRITE)))
            .thenReturn(beneficioDestino);
        
        // Act
        assertDoesNotThrow(() -> service.transfer(fromId, toId, amount));
        
        // Assert
        assertEquals(new BigDecimal("750.00"), beneficioOrigem.getValor(), 
            "Saldo origem deve ser 1000 - 250 = 750");
        assertEquals(new BigDecimal("750.00"), beneficioDestino.getValor(), 
            "Saldo destino deve ser 500 + 250 = 750");
        
        verify(entityManager).find(eq(Beneficio.class), eq(1L), eq(LockModeType.PESSIMISTIC_WRITE));
        verify(entityManager).find(eq(Beneficio.class), eq(2L), eq(LockModeType.PESSIMISTIC_WRITE));
        verify(entityManager).merge(beneficioOrigem);
        verify(entityManager).merge(beneficioDestino);
    }
    
    @Test
    @DisplayName("Deve lançar SaldoInsuficienteException quando saldo origem for insuficiente")
    void testTransferInsufficientBalance() {
        // Arrange
        Long fromId = 1L;
        Long toId = 2L;
        BigDecimal amount = new BigDecimal("1500.00"); // Maior que saldo de 1000
        
        when(entityManager.find(eq(Beneficio.class), eq(1L), eq(LockModeType.PESSIMISTIC_WRITE)))
            .thenReturn(beneficioOrigem);
        when(entityManager.find(eq(Beneficio.class), eq(2L), eq(LockModeType.PESSIMISTIC_WRITE)))
            .thenReturn(beneficioDestino);
        
        // Act & Assert
        SaldoInsuficienteException exception = assertThrows(
            SaldoInsuficienteException.class,
            () -> service.transfer(fromId, toId, amount),
            "Deve lançar SaldoInsuficienteException"
        );
        
        assertEquals(new BigDecimal("1000.00"), exception.getSaldoDisponivel());
        assertEquals(new BigDecimal("1500.00"), exception.getValorSolicitado());
        
        // Verifica que nenhum merge foi executado
        verify(entityManager, never()).merge(any());
    }
    
    @Test
    @DisplayName("Deve lançar BeneficioNaoEncontradoException quando benefício origem não existe")
    void testTransferBeneficioOrigemNotFound() {
        // Arrange
        Long fromId = 999L; // Não existe
        Long toId = 2L;
        BigDecimal amount = new BigDecimal("100.00");
        
        when(entityManager.find(eq(Beneficio.class), eq(2L), eq(LockModeType.PESSIMISTIC_WRITE)))
            .thenReturn(beneficioDestino);
        when(entityManager.find(eq(Beneficio.class), eq(999L), eq(LockModeType.PESSIMISTIC_WRITE)))
            .thenReturn(null); // Não encontrado
        
        // Act & Assert
        BeneficioNaoEncontradoException exception = assertThrows(
            BeneficioNaoEncontradoException.class,
            () -> service.transfer(fromId, toId, amount),
            "Deve lançar BeneficioNaoEncontradoException"
        );
        
        assertEquals(999L, exception.getBeneficioId());
        
        verify(entityManager, never()).merge(any());
    }
    
    @Test
    @DisplayName("Deve lançar BeneficioNaoEncontradoException quando benefício destino não existe")
    void testTransferBeneficioDestinoNotFound() {
        // Arrange
        Long fromId = 1L;
        Long toId = 999L; // Não existe
        BigDecimal amount = new BigDecimal("100.00");
        
        when(entityManager.find(eq(Beneficio.class), eq(1L), eq(LockModeType.PESSIMISTIC_WRITE)))
            .thenReturn(beneficioOrigem);
        when(entityManager.find(eq(Beneficio.class), eq(999L), eq(LockModeType.PESSIMISTIC_WRITE)))
            .thenReturn(null); // Não encontrado
        
        // Act & Assert
        BeneficioNaoEncontradoException exception = assertThrows(
            BeneficioNaoEncontradoException.class,
            () -> service.transfer(fromId, toId, amount),
            "Deve lançar BeneficioNaoEncontradoException"
        );
        
        assertEquals(999L, exception.getBeneficioId());
        
        verify(entityManager, never()).merge(any());
    }
    
    @Test
    @DisplayName("Deve lançar BeneficioInativoException quando benefício origem está inativo")
    void testTransferBeneficioOrigemInativo() {
        // Arrange
        Long fromId = 1L;
        Long toId = 2L;
        BigDecimal amount = new BigDecimal("100.00");
        
        beneficioOrigem.setAtivo(false); // Inativo
        
        when(entityManager.find(eq(Beneficio.class), eq(1L), eq(LockModeType.PESSIMISTIC_WRITE)))
            .thenReturn(beneficioOrigem);
        when(entityManager.find(eq(Beneficio.class), eq(2L), eq(LockModeType.PESSIMISTIC_WRITE)))
            .thenReturn(beneficioDestino);
        
        // Act & Assert
        BeneficioInativoException exception = assertThrows(
            BeneficioInativoException.class,
            () -> service.transfer(fromId, toId, amount),
            "Deve lançar BeneficioInativoException"
        );
        
        assertEquals(1L, exception.getBeneficioId());
        assertEquals("origem", exception.getTipo());
        
        verify(entityManager, never()).merge(any());
    }
    
    @Test
    @DisplayName("Deve lançar BeneficioInativoException quando benefício destino está inativo")
    void testTransferBeneficioDestinoInativo() {
        // Arrange
        Long fromId = 1L;
        Long toId = 2L;
        BigDecimal amount = new BigDecimal("100.00");
        
        beneficioDestino.setAtivo(false); // Inativo
        
        when(entityManager.find(eq(Beneficio.class), eq(1L), eq(LockModeType.PESSIMISTIC_WRITE)))
            .thenReturn(beneficioOrigem);
        when(entityManager.find(eq(Beneficio.class), eq(2L), eq(LockModeType.PESSIMISTIC_WRITE)))
            .thenReturn(beneficioDestino);
        
        // Act & Assert
        BeneficioInativoException exception = assertThrows(
            BeneficioInativoException.class,
            () -> service.transfer(fromId, toId, amount),
            "Deve lançar BeneficioInativoException"
        );
        
        assertEquals(2L, exception.getBeneficioId());
        assertEquals("destino", exception.getTipo());
        
        verify(entityManager, never()).merge(any());
    }
    
    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando valor é negativo")
    void testTransferNegativeAmount() {
        // Arrange
        Long fromId = 1L;
        Long toId = 2L;
        BigDecimal amount = new BigDecimal("-100.00"); // Negativo
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> service.transfer(fromId, toId, amount),
            "Deve lançar IllegalArgumentException"
        );
        
        assertTrue(exception.getMessage().contains("positivo"));
        
        verify(entityManager, never()).find(any(Class.class), any(), any(LockModeType.class));
        verify(entityManager, never()).merge(any());
    }
    
    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando valor é zero")
    void testTransferZeroAmount() {
        // Arrange
        Long fromId = 1L;
        Long toId = 2L;
        BigDecimal amount = BigDecimal.ZERO; // Zero
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> service.transfer(fromId, toId, amount),
            "Deve lançar IllegalArgumentException"
        );
        
        assertTrue(exception.getMessage().contains("positivo"));
        
        verify(entityManager, never()).find(any(Class.class), any(), any(LockModeType.class));
        verify(entityManager, never()).merge(any());
    }
    
    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando IDs origem e destino são iguais")
    void testTransferSameId() {
        // Arrange
        Long fromId = 1L;
        Long toId = 1L; // Mesmo ID
        BigDecimal amount = new BigDecimal("100.00");
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> service.transfer(fromId, toId, amount),
            "Deve lançar IllegalArgumentException"
        );
        
        assertTrue(exception.getMessage().contains("não podem ser iguais"));
        
        verify(entityManager, never()).find(any(Class.class), any(), any(LockModeType.class));
        verify(entityManager, never()).merge(any());
    }
    
    @Test
    @DisplayName("Deve adquirir locks na ordem crescente de IDs (lock ordering)")
    void testTransferLockOrder() {
        // Arrange - IDs invertidos: from=2, to=1
        Long fromId = 2L;
        Long toId = 1L;
        BigDecimal amount = new BigDecimal("100.00");
        
        // Inverte os benefícios para simular IDs trocados
        beneficioOrigem.setId(2L);
        beneficioDestino.setId(1L);
        
        when(entityManager.find(eq(Beneficio.class), eq(1L), eq(LockModeType.PESSIMISTIC_WRITE)))
            .thenReturn(beneficioDestino);
        when(entityManager.find(eq(Beneficio.class), eq(2L), eq(LockModeType.PESSIMISTIC_WRITE)))
            .thenReturn(beneficioOrigem);
        
        // Act
        assertDoesNotThrow(() -> service.transfer(fromId, toId, amount));
        
        // Assert - Verifica que lock foi adquirido na ordem: ID 1 primeiro, ID 2 depois
        var inOrder = inOrder(entityManager);
        inOrder.verify(entityManager).find(eq(Beneficio.class), eq(1L), eq(LockModeType.PESSIMISTIC_WRITE));
        inOrder.verify(entityManager).find(eq(Beneficio.class), eq(2L), eq(LockModeType.PESSIMISTIC_WRITE));
        
        // Verifica que a transferência funcionou corretamente mesmo com IDs invertidos
        assertEquals(new BigDecimal("900.00"), beneficioOrigem.getValor());
        assertEquals(new BigDecimal("600.00"), beneficioDestino.getValor());
    }
    
    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando fromId é nulo")
    void testTransferNullFromId() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> service.transfer(null, 2L, new BigDecimal("100.00")),
            "Deve lançar IllegalArgumentException"
        );
        
        assertTrue(exception.getMessage().contains("origem não pode ser nulo"));
    }
    
    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando toId é nulo")
    void testTransferNullToId() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> service.transfer(1L, null, new BigDecimal("100.00")),
            "Deve lançar IllegalArgumentException"
        );
        
        assertTrue(exception.getMessage().contains("destino não pode ser nulo"));
    }
    
    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando amount é nulo")
    void testTransferNullAmount() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> service.transfer(1L, 2L, null),
            "Deve lançar IllegalArgumentException"
        );
        
        assertTrue(exception.getMessage().contains("transferência não pode ser nulo"));
    }
}
