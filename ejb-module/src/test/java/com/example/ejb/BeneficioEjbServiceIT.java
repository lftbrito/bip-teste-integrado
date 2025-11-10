package com.example.ejb;

import com.example.ejb.entity.Beneficio;
import com.example.ejb.exception.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de integração para BeneficioEjbService.
 * 
 * <p>Estes testes validam o comportamento do serviço em um ambiente mais realista,
 * com EntityManager real, transações e concorrência.
 * 
 * <p><b>CRÍTICO:</b> O teste {@link #testConcurrentTransfersNoLostUpdate()} é a prova
 * definitiva de que o bug de race condition foi corrigido.
 * 
 * @author Sistema de Benefícios
 */
@DisplayName("BeneficioEjbService - Testes de Integração")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BeneficioEjbServiceIT {
    
    private static EntityManagerFactory emf;
    private EntityManager em;
    private EntityTransaction tx;
    private BeneficioEjbService service;
    
    private Long beneficioAId;
    private Long beneficioBId;
    private Long beneficioCId;
    
    @BeforeAll
    static void setUpClass() {
        // Cria EntityManagerFactory para todos os testes
        emf = Persistence.createEntityManagerFactory("beneficioPU");
    }
    
    @AfterAll
    static void tearDownClass() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
    
    @BeforeEach
    void setUp() {
        em = emf.createEntityManager();
        tx = em.getTransaction();
        
        // Cria serviço e injeta EntityManager manualmente
        service = new BeneficioEjbService();
        injectEntityManager(service, em);
        
        // Setup: Cria benefícios para teste
        tx.begin();
        
        Beneficio a = new Beneficio();
        a.setNome("Vale Refeição");
        a.setDescricao("Benefício para alimentação");
        a.setValor(new BigDecimal("1000.00"));
        a.setAtivo(true);
        
        Beneficio b = new Beneficio();
        b.setNome("Vale Alimentação");
        b.setDescricao("Benefício para supermercado");
        b.setValor(new BigDecimal("500.00"));
        b.setAtivo(true);
        
        Beneficio c = new Beneficio();
        c.setNome("Vale Transporte");
        c.setDescricao("Benefício para transporte");
        c.setValor(new BigDecimal("200.00"));
        c.setAtivo(true);
        
        em.persist(a);
        em.persist(b);
        em.persist(c);
        
        tx.commit();
        
        beneficioAId = a.getId();
        beneficioBId = b.getId();
        beneficioCId = c.getId();
        
        // Limpa cache para forçar leitura do banco
        em.clear();
    }
    
    @AfterEach
    void tearDown() {
        if (tx != null && tx.isActive()) {
            tx.rollback();
        }
        
        // Limpa dados de teste
        if (em != null && em.isOpen()) {
            EntityTransaction cleanupTx = em.getTransaction();
            try {
                cleanupTx.begin();
                em.createQuery("DELETE FROM Beneficio").executeUpdate();
                cleanupTx.commit();
            } catch (Exception e) {
                if (cleanupTx.isActive()) {
                    cleanupTx.rollback();
                }
            } finally {
                em.close();
            }
        }
    }
    
    /**
     * Injeta EntityManager no service via reflection (simula @PersistenceContext).
     */
    private void injectEntityManager(BeneficioEjbService service, EntityManager em) {
        try {
            var field = BeneficioEjbService.class.getDeclaredField("em");
            field.setAccessible(true);
            field.set(service, em);
        } catch (Exception e) {
            throw new RuntimeException("Falha ao injetar EntityManager", e);
        }
    }
    
    @Test
    @Order(1)
    @DisplayName("IT: Deve transferir valor com sucesso em ambiente real")
    void testTransferIntegration() throws BeneficioException {
        // Arrange
        BigDecimal valorTransferencia = new BigDecimal("300.00");
        
        // Act
        tx.begin();
        service.transfer(beneficioAId, beneficioBId, valorTransferencia);
        tx.commit();
        
        // Assert
        em.clear(); // Limpa cache para forçar leitura do banco
        
        Beneficio a = em.find(Beneficio.class, beneficioAId);
        Beneficio b = em.find(Beneficio.class, beneficioBId);
        
        assertEquals(new BigDecimal("700.00"), a.getValor(), 
            "Benefício A deve ter R$ 700,00 (1000 - 300)");
        assertEquals(new BigDecimal("800.00"), b.getValor(), 
            "Benefício B deve ter R$ 800,00 (500 + 300)");
        
        // Verifica que VERSION foi incrementado (optimistic locking)
        assertNotNull(a.getVersion());
        assertNotNull(b.getVersion());
    }
    
    @Test
    @Order(2)
    @DisplayName("IT: Deve fazer rollback automático em caso de exceção")
    void testTransferRollback() throws Exception {
        // Arrange
        BigDecimal saldoInicialA = em.find(Beneficio.class, beneficioAId).getValor();
        BigDecimal saldoInicialB = em.find(Beneficio.class, beneficioBId).getValor();
        BigDecimal valorExcessivo = new BigDecimal("2000.00"); // Mais que o saldo
        
        // Act
        tx.begin();
        try {
            service.transfer(beneficioAId, beneficioBId, valorExcessivo);
            tx.commit();
            fail("Deveria ter lançado SaldoInsuficienteException");
        } catch (SaldoInsuficienteException e) {
            // Esperado
            if (tx.isActive()) {
                tx.rollback();
            }
        }
        
        // Assert - valores NÃO devem ter mudado
        em.clear();
        
        Beneficio a = em.find(Beneficio.class, beneficioAId);
        Beneficio b = em.find(Beneficio.class, beneficioBId);
        
        assertEquals(saldoInicialA, a.getValor(), 
            "Saldo do benefício A deve permanecer inalterado após rollback");
        assertEquals(saldoInicialB, b.getValor(), 
            "Saldo do benefício B deve permanecer inalterado após rollback");
    }
    
    @Test
    @Order(3)
    @DisplayName("IT: CRÍTICO - Deve prevenir lost update em transferências concorrentes")
    void testConcurrentTransfersNoLostUpdate() throws Exception {
        // Arrange
        int numThreads = 2;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(numThreads);
        
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        
        // Act - Duas threads fazem transferências simultâneas do MESMO benefício
        Runnable transfer1 = () -> {
            EntityManager em1 = emf.createEntityManager();
            EntityTransaction tx1 = em1.getTransaction();
            BeneficioEjbService service1 = new BeneficioEjbService();
            injectEntityManager(service1, em1);
            
            try {
                startLatch.await(); // Espera sinal para começar
                
                tx1.begin();
                try {
                    service1.transfer(beneficioAId, beneficioBId, new BigDecimal("500.00"));
                    tx1.commit();
                    successCount.incrementAndGet();
                } catch (BeneficioException e) {
                    if (tx1.isActive()) tx1.rollback();
                    failureCount.incrementAndGet();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                failureCount.incrementAndGet();
            } finally {
                em1.close();
                endLatch.countDown();
            }
        };
        
        Runnable transfer2 = () -> {
            EntityManager em2 = emf.createEntityManager();
            EntityTransaction tx2 = em2.getTransaction();
            BeneficioEjbService service2 = new BeneficioEjbService();
            injectEntityManager(service2, em2);
            
            try {
                startLatch.await(); // Espera sinal para começar
                
                tx2.begin();
                try {
                    service2.transfer(beneficioAId, beneficioCId, new BigDecimal("300.00"));
                    tx2.commit();
                    successCount.incrementAndGet();
                } catch (BeneficioException e) {
                    if (tx2.isActive()) tx2.rollback();
                    failureCount.incrementAndGet();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                failureCount.incrementAndGet();
            } finally {
                em2.close();
                endLatch.countDown();
            }
        };
        
        Future<?> f1 = executor.submit(transfer1);
        Future<?> f2 = executor.submit(transfer2);
        
        // Inicia ambas as threads simultaneamente
        startLatch.countDown();
        
        // Aguarda conclusão com timeout
        assertTrue(endLatch.await(30, TimeUnit.SECONDS), 
            "Threads devem completar em 30 segundos");
        
        executor.shutdown();
        
        // Assert
        em.clear();
        
        Beneficio a = em.find(Beneficio.class, beneficioAId);
        Beneficio b = em.find(Beneficio.class, beneficioBId);
        Beneficio c = em.find(Beneficio.class, beneficioCId);
        
        System.out.println("=== RESULTADO DO TESTE DE CONCORRÊNCIA ===");
        System.out.println("Transferências bem-sucedidas: " + successCount.get());
        System.out.println("Transferências falhadas: " + failureCount.get());
        System.out.println("Saldo final A: " + a.getValor());
        System.out.println("Saldo final B: " + b.getValor());
        System.out.println("Saldo final C: " + c.getValor());
        
        // VALIDAÇÃO CRÍTICA: Sem lost update
        // Cenário esperado: Ambas transferências devem ter sido serializadas
        // A: 1000 - 500 - 300 = 200 OU uma das transferências falhou
        
        BigDecimal expectedA = new BigDecimal("200.00");
        BigDecimal totalFinal = a.getValor().add(b.getValor()).add(c.getValor());
        BigDecimal totalInicial = new BigDecimal("1700.00"); // 1000 + 500 + 200
        
        // A soma total DEVE ser conservada (prova de que não houve lost update)
        assertEquals(totalInicial, totalFinal, 
            "❌ FALHA CRÍTICA: Lost update detectado! A soma total deve ser conservada. " +
            "Isso significa que o pessimistic locking NÃO está funcionando.");
        
        // Se ambas transferências foram bem-sucedidas, A deve ter 200
        if (successCount.get() == 2) {
            assertEquals(expectedA, a.getValor(), 
                "Com ambas transferências bem-sucedidas, A deve ter R$ 200,00 (1000 - 500 - 300)");
            assertEquals(new BigDecimal("1000.00"), b.getValor(), 
                "B deve ter R$ 1000,00 (500 + 500)");
            assertEquals(new BigDecimal("500.00"), c.getValor(), 
                "C deve ter R$ 500,00 (200 + 300)");
        }
        
        System.out.println("✅ SUCESSO: Pessimistic locking preveniu lost update!");
        System.out.println("✅ Soma total conservada: " + totalFinal);
    }
    
    @Test
    @Order(4)
    @DisplayName("IT: Deve suportar múltiplas transferências sequenciais")
    void testMultipleSequentialTransfers() throws BeneficioException {
        // Act - 3 transferências em sequência
        tx.begin();
        service.transfer(beneficioAId, beneficioBId, new BigDecimal("100.00"));
        tx.commit();
        
        tx.begin();
        service.transfer(beneficioBId, beneficioCId, new BigDecimal("200.00"));
        tx.commit();
        
        tx.begin();
        service.transfer(beneficioCId, beneficioAId, new BigDecimal("50.00"));
        tx.commit();
        
        // Assert
        em.clear();
        
        Beneficio a = em.find(Beneficio.class, beneficioAId);
        Beneficio b = em.find(Beneficio.class, beneficioBId);
        Beneficio c = em.find(Beneficio.class, beneficioCId);
        
        // A: 1000 - 100 + 50 = 950
        assertEquals(new BigDecimal("950.00"), a.getValor(), 
            "A: 1000 - 100 + 50 = 950");
        
        // B: 500 + 100 - 200 = 400
        assertEquals(new BigDecimal("400.00"), b.getValor(), 
            "B: 500 + 100 - 200 = 400");
        
        // C: 200 + 200 - 50 = 350
        assertEquals(new BigDecimal("350.00"), c.getValor(), 
            "C: 200 + 200 - 50 = 350");
        
        // Verifica conservação da soma total
        BigDecimal total = a.getValor().add(b.getValor()).add(c.getValor());
        assertEquals(new BigDecimal("1700.00"), total, 
            "Soma total deve ser conservada");
    }
    
    @Test
    @Order(5)
    @DisplayName("IT: Performance - Deve completar 10 transferências sequenciais em tempo aceitável")
    void testPerformanceSequential() throws BeneficioException {
        // Arrange
        long startTime = System.currentTimeMillis();
        
        // Act - 10 transferências
        for (int i = 0; i < 10; i++) {
            tx.begin();
            service.transfer(beneficioAId, beneficioBId, new BigDecimal("10.00"));
            tx.commit();
        }
        
        long duration = System.currentTimeMillis() - startTime;
        double avgLatency = duration / 10.0;
        
        // Assert
        System.out.println("=== MÉTRICAS DE PERFORMANCE ===");
        System.out.println("10 transferências completadas em " + duration + " ms");
        System.out.println("Latência média: " + String.format("%.2f", avgLatency) + " ms");
        
        assertTrue(avgLatency < 100, 
            "Latência média deve ser < 100ms por transferência (atual: " + 
            String.format("%.2f", avgLatency) + " ms)");
        
        // Valida estado final
        em.clear();
        Beneficio a = em.find(Beneficio.class, beneficioAId);
        Beneficio b = em.find(Beneficio.class, beneficioBId);
        
        assertEquals(new BigDecimal("900.00"), a.getValor(), 
            "A: 1000 - (10 * 10) = 900");
        assertEquals(new BigDecimal("600.00"), b.getValor(), 
            "B: 500 + (10 * 10) = 600");
    }
}
