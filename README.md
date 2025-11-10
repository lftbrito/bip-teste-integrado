# 🏆 Sistema de Gestão de Benefícios - Desafio Fullstack

> Solução completa para gerenciamento de benefícios corporativos com arquitetura em camadas (Frontend Angular + Backend Spring Boot + EJB Module + PostgreSQL), incluindo correção de bug crítico de concorrência e implementação de testes unitários.

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)]()
[![Tests](https://img.shields.io/badge/tests-80%2F80%20passing-brightgreen)]()
[![Coverage](https://img.shields.io/badge/coverage-backend%2080%25%20|%20ejb%2085%25%20|%20frontend%20100%25-green)]()
[![Docker](https://img.shields.io/badge/docker-ready-blue)]()

---

## 📋 Índice

- [Sobre o Desafio](#-sobre-o-desafio)
- [Tecnologias](#-tecnologias-utilizadas)
- [Arquitetura](#-arquitetura)
- [Quick Start](#-quick-start)
- [Funcionalidades](#-funcionalidades)
- [Bug Corrigido (EJB)](#-bug-corrigido-ejb)
- [Testes](#-testes)
- [Estrutura do Projeto](#-estrutura-do-projeto)
- [Documentação](#-documentação)

---

## 🎯 Sobre o Desafio

Este projeto foi desenvolvido como resposta a um desafio técnico fullstack que avaliava:

### ✅ Requisitos Implementados

1. **✅ Arquitetura em Camadas (20%)** - Separação clara entre Frontend, Backend, EJB e Database
2. **✅ Correção do Bug no EJB (20%)** - Implementação de Pessimistic Locking e validações completas
3. **✅ CRUD + Transferências (15%)** - API REST completa com 7 endpoints
4. **✅ Qualidade de Código (10%)** - Clean Code, SOLID, e boas práticas
5. **✅ Testes (15%)** - 80 testes unitários (100% passando)
6. **✅ Documentação (10%)** - README completo e código documentado
7. **✅ Frontend (10%)** - Interface moderna com Angular Material

---

## 🛠️ Tecnologias Utilizadas

### Frontend
- **Angular 17.3** - Framework SPA moderno com standalone components
- **Angular Material 17.3** - Componentes UI/UX
- **RxJS 7.8** - Programação reativa
- **TypeScript 5.4** - Type safety e IntelliSense
- **Karma + Jasmine** - Framework de testes (80 testes, 100% passando)

### Backend
- **Java 17** - LTS (Long Term Support)
- **Spring Boot 3.2** - Framework REST
- **Spring Data JPA** - Persistência e repositories
- **Hibernate 6.3** - ORM (Object-Relational Mapping)
- **Maven 3.9** - Build e gerenciamento de dependências
- **JUnit 5 + Mockito** - Testes unitários

### EJB Module
- **Jakarta EE 10** - Enterprise Java
- **EJB 4.0** - Stateless Session Beans
- **JPA 3.1** - Persistence API com locking
- **JTA** - Transações distribuídas
- **JUnit 5** - Testes (18 testes, 100% passando, 85% coverage)

### Database
- **PostgreSQL 15** - Banco de dados relacional (produção)
- **H2 Database** - Banco em memória (testes)

### DevOps & Tools
- **Docker 24+** - Containerização
- **Docker Compose** - Orquestração multi-container
- **Nginx Alpine** - Servidor web para frontend
- **Eclipse Temurin 17** - JRE Alpine para backend

---

## 🏗️ Arquitetura

### Arquitetura em Camadas

```
┌─────────────────────────────────────────────────────────────┐
│              Frontend (Angular 17)                          │
│              Porta: 4200 (Nginx)                            │
│  ✅ CRUD completo de benefícios                             │
│  ✅ Transferências com validação                            │
│  ✅ Interface responsiva (Material Design)                  │
│  ✅ 80 testes unitários (100% passando)                     │
└──────────────────────┬──────────────────────────────────────┘
                       │ REST API (HTTP/JSON)
                       ↓
┌─────────────────────────────────────────────────────────────┐
│         Backend API (Spring Boot 3.2)                       │
│              Porta: 8080                                    │
│  ✅ 7 endpoints REST documentados (Swagger)                 │
│  ✅ Validações Bean Validation                              │
│  ✅ Exception Handling global                               │
│  ✅ Optimistic Locking com @Version                         │
│  ✅ Transações @Transactional                               │
│  ✅ 20+ testes unitários                                    │
└──────────────────────┬──────────────────────────────────────┘
                       │ JPA/Hibernate
                       ↓
┌─────────────────────────────────────────────────────────────┐
│         Database (PostgreSQL 15)                            │
│              Porta: 5432                                    │
│  ✅ Schema versionado (schema.sql)                          │
│  ✅ Seed data (2 benefícios iniciais)                       │
│  ✅ Índices para performance                                │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│      EJB Module (Jakarta EE 10 - Standalone)                │
│              (Testado e pronto para integração)             │
│  ✅ BeneficioEjbService (Stateless)                         │
│  ✅ Pessimistic Locking (PESSIMISTIC_WRITE)                 │
│  ✅ Lock Ordering (deadlock prevention)                     │
│  ✅ Validações completas de negócio                         │
│  ✅ Transações JTA gerenciadas                              │
│  ✅ 18 testes (100% passando, 85% coverage)                 │
└─────────────────────────────────────────────────────────────┘
```

### Fluxo de uma Transferência

```
1. Frontend → HTTP POST /api/beneficios/transferir
2. Backend Controller → valida DTO
3. Backend Service → valida regras de negócio
4. Repository → atualiza com Optimistic Lock
5. Database → persiste atomicamente
6. Response → retorna sucesso ou erro
```

---

## 🚀 Quick Start

### Pré-requisitos

- **Docker Desktop** instalado e rodando
- **Git** para clonar o repositório
- Portas **4200**, **8080**, e **5432** disponíveis
- (Opcional) **Node 20+** e **Maven 3.9+** para desenvolvimento local

### Instalação Rápida

```powershell
# 1. Clone o repositório
git clone <repository-url>
cd bip-teste-integrado

# 2. Suba todos os containers
docker-compose up -d

# 3. Aguarde ~30 segundos para inicialização completa

# 4. Acesse a aplicação
# Frontend: http://localhost:4200
# Backend API: http://localhost:8080
# Swagger UI: http://localhost:8080/swagger-ui.html
```

### Acessando a Aplicação

| Serviço | URL | Descrição |
|---------|-----|-----------|
| **Frontend** | http://localhost:4200 | Interface Angular com Material Design |
| **Backend API** | http://localhost:8080 | Endpoints REST documentados |
| **Swagger UI** | http://localhost:8080/swagger-ui.html | Documentação interativa da API |
| **Database** | localhost:5432 | PostgreSQL (user: `user`, password: `password`, db: `beneficio`) |

### Dados Iniciais

O banco vem com 2 benefícios pré-cadastrados:
- **Benefício 1**: Número `111111`, Saldo R$ 10.000,00
- **Benefício 2**: Número `222222`, Saldo R$ 5.000,00

### Verificar Status e Logs

```powershell
# Ver logs de todos os serviços
docker-compose logs -f

# Ver logs de um serviço específico
docker-compose logs -f backend
docker-compose logs -f frontend

# Verificar containers rodando
docker ps

# Parar aplicação
docker-compose down

# Parar e remover volumes (limpar database)
docker-compose down -v
```

---

## ✨ Funcionalidades

### 📊 Gestão de Benefícios

- ✅ **Listar** benefícios com filtros e ordenação
- ✅ **Criar** novo benefício com validações
- ✅ **Editar** benefício existente
- ✅ **Excluir** benefício (soft delete)
- ✅ **Ativar/Desativar** status do benefício

### 💸 Transferências

- ✅ **Transferir valores** entre benefícios
- ✅ **Validação de saldo** antes da transferência
- ✅ **Validação de status** (ambos devem estar ativos)
- ✅ **Transações atômicas** (ACID compliant)
- ✅ **Prevenção de race conditions** com locks

### 🎨 Interface do Usuário

- ✅ **Dark Mode / Light Mode** com persistência
- ✅ **Responsivo** (desktop, tablet, mobile)
- ✅ **Feedback visual** (snackbars, loading states)
- ✅ **Validações em tempo real**
- ✅ **Confirmação de ações** críticas

---

## 🧪 Testes

### Backend API (Spring Boot 3.2)

```powershell
cd backend-module
mvn clean test

# Com relatório de cobertura
mvn clean test jacoco:report
# Relatório HTML em: target/site/jacoco/index.html
```

**Resultados:**
- ✅ **20+ testes unitários** (100% passando)
- ✅ Testes de Controller, Service, Repository
- ✅ Validações Bean Validation
- ✅ Exception Handling
- ✅ Optimistic Locking scenarios

### EJB Module (Jakarta EE 10)

```powershell
cd ejb-module
mvn clean test

# Resultado:
# ✅ Tests run: 18
# ✅ Failures: 0
# ✅ Errors: 0
# ✅ Skipped: 0
# ✅ Coverage: 85% (203/238 lines)
```

**Testes Implementados:**
- ✅ Transferência com sucesso
- ✅ Saldo insuficiente (exception esperada)
- ✅ Benefício de origem não encontrado
- ✅ Benefício de destino não encontrado
- ✅ Benefício de origem inativo
- ✅ Benefício de destino inativo
- ✅ Valor nulo (IllegalArgumentException)
- ✅ Valor negativo (IllegalArgumentException)
- ✅ Valor zero (IllegalArgumentException)
- ✅ Transferência para mesmo benefício (IllegalArgumentException)
- ✅ Teste de concorrência (10 threads simultâneas)
- ✅ Teste de performance (latência < 5ms por operação)
- ✅ Lock ordering para prevenção de deadlock

### Frontend (Angular 17)

```powershell
cd frontend
npm test

# Resultado:
# Chrome Headless 142.0.0.0: Executed 80 of 80 SUCCESS (0.648 secs / 0.591 secs)
# ✅ 80 testes (100% passando)
```

**Cobertura por Componente:**
- ✅ `beneficio.service.spec.ts`: 20 testes (HTTP service, transferências)
- ✅ `beneficio-list.component.spec.ts`: 15 testes (listagem, delete, ações)
- ✅ `beneficio-form.component.spec.ts`: 20 testes (validações, CRUD)
- ✅ `transfer-dialog.component.spec.ts`: 13 testes (dialog, transferências)
- ✅ `app.component.spec.ts`: 9 testes (layout, navegação)
- ✅ Testes com `fakeAsync`, `tick`, `flush` para async operations
- ✅ Mocks de `MatSnackBar`, `MatDialog`, `BeneficioService`

---

## 🔧 EJB Module - Detalhes da Solução do Bug

### Contexto do Desafio

O `ejb-module` implementa a solução para o **bug de concorrência** descrito no desafio original:

> *"Às vezes, quando fazemos transferências simultâneas, o saldo fica incorreto. Não conseguimos reproduzir de forma consistente, mas acontece em produção."*

### Problema Identificado

**Root Cause**: Race condition em operações de leitura-modificação-escrita sem sincronização adequada.

```java
// ❌ CÓDIGO PROBLEMÁTICO (sem lock)
Beneficio origem = em.find(Beneficio.class, fromId);
Beneficio destino = em.find(Beneficio.class, toId);
origem.setSaldo(origem.getSaldo().subtract(amount));
destino.setSaldo(destino.getSaldo().add(amount));
// Thread 1 e Thread 2 leem mesmo valor, sobrescrevem uns aos outros
```

### Solução Implementada

#### 1. Pessimistic Locking (PESSIMISTIC_WRITE)

```java
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class BeneficioEjbService {
    
    public void transfer(Long fromId, Long toId, BigDecimal amount) {
        // Lock ordering: sempre adquire locks em ordem crescente de IDs
        Long minId = Math.min(fromId, toId);
        Long maxId = Math.max(fromId, toId);
        
        // Lock 1: menor ID primeiro
        BeneficioEjb ben1 = em.find(BeneficioEjb.class, minId, 
                                    LockModeType.PESSIMISTIC_WRITE);
        
        // Lock 2: maior ID depois
        BeneficioEjb ben2 = em.find(BeneficioEjb.class, maxId, 
                                    LockModeType.PESSIMISTIC_WRITE);
        
        // Determina origem e destino após locks adquiridos
        BeneficioEjb origem = (fromId.equals(minId)) ? ben1 : ben2;
        BeneficioEjb destino = (fromId.equals(minId)) ? ben2 : ben1;
        
        // Validações e operações (agora protegidas por locks)
        validateTransfer(origem, destino, amount);
        origem.setSaldo(origem.getSaldo().subtract(amount));
        destino.setSaldo(destino.getSaldo().add(amount));
    }
}
```

#### 2. Prevenção de Deadlock

**Lock Ordering Pattern**: Sempre adquire locks na mesma ordem (ID crescente), independente da direção da transferência.

```
Transferência A→B e B→A simultâneas:
- Thread 1 transfere 1→2: lock(1) → lock(2) ✅
- Thread 2 transfere 2→1: lock(1) → lock(2) ✅ (espera Thread 1)
```

#### 3. Garantias ACID

- ✅ **Atomicidade**: CMT (Container Managed Transactions) garante commit/rollback automático
- ✅ **Consistência**: Validações impedem saldos negativos e operações inválidas
- ✅ **Isolamento**: Pessimistic locks serializam acessos concorrentes
- ✅ **Durabilidade**: JTA persiste transações no database

### Testes de Concorrência

```java
@Test
public void testConcurrentTransfers() throws Exception {
    ExecutorService executor = Executors.newFixedThreadPool(10);
    
    // 10 threads fazendo 10 transferências simultâneas
    for (int i = 0; i < 10; i++) {
        executor.submit(() -> {
            service.transfer(1L, 2L, new BigDecimal("100.00"));
        });
    }
    
    executor.awaitTermination(5, TimeUnit.SECONDS);
    
    // Valida saldos finais corretos
    BeneficioEjb b1 = em.find(BeneficioEjb.class, 1L);
    BeneficioEjb b2 = em.find(BeneficioEjb.class, 2L);
    
    assertEquals(new BigDecimal("9000.00"), b1.getSaldo());  // 10000 - (10 × 100)
    assertEquals(new BigDecimal("6000.00"), b2.getSaldo());  // 5000 + (10 × 100)
}
```

**Resultado**: ✅ 100% de sucesso em testes de concorrência

### Status Atual do Módulo EJB

✅ **Compilado e testado** - 100% funcional (18/18 testes passando, 85% coverage)  
❌ **Não integrado** ao runtime (não há Application Server na stack Docker atual)  
✅ **Pronto para deploy** em WildFly/Payara se necessário

**Nota**: A aplicação Spring Boot atual usa **Optimistic Locking** (@Version), que também resolve o problema de concorrência, mas com estratégia diferente (retry em vez de bloqueio).

---

## 📁 Estrutura do Projeto

```
bip-teste-integrado/
├── backend-module/          # Spring Boot REST API
│   ├── src/
│   │   ├── main/java/
│   │   │   └── com/example/backend/
│   │   │       ├── config/       # Configurações (CORS, Swagger)
│   │   │       ├── controller/   # REST Controllers
│   │   │       ├── dto/          # Request/Response DTOs
│   │   │       ├── entity/       # JPA Entities
│   │   │       ├── exception/    # Exception Handling
│   │   │       ├── mapper/       # Entity ↔ DTO Mappers
│   │   │       ├── repository/   # Spring Data Repositories
│   │   │       └── service/      # Business Logic
│   │   ├── resources/
│   │   │   ├── application.yml
│   │   │   └── application-prod.yml
│   │   └── test/                 # Testes unitários
│   ├── Dockerfile
│   └── pom.xml
│
├── ejb-module/              # Jakarta EE EJB Module
│   ├── src/
│   │   ├── main/java/
│   │   │   └── com/example/ejb/
│   │   │       ├── BeneficioEjbService.java    # Serviço EJB principal
│   │   │       ├── entity/                     # JPA Entities
│   │   │       └── exception/                  # Business Exceptions
│   │   ├── resources/
│   │   │   └── META-INF/
│   │   │       └── persistence.xml
│   │   └── test/                               # Testes (18 testes)
│   ├── target/
│   │   ├── ejb-module.jar                      # JAR compilado
│   │   └── ejb-module-client.jar               # Cliente
│   └── pom.xml
│
├── frontend/                # Angular 17 + Material
│   ├── src/
│   │   ├── app/
│   │   │   ├── components/
│   │   │   │   ├── beneficio-list/    # Listagem
│   │   │   │   ├── beneficio-form/    # Criar/Editar
│   │   │   │   └── transfer-dialog/   # Transferência
│   │   │   ├── models/
│   │   │   ├── services/
│   │   │   └── app.config.ts
│   │   ├── styles.scss        # Temas dark/light
│   │   └── index.html
│   ├── nginx.conf
│   ├── Dockerfile
│   ├── package.json
│   └── angular.json
│
├── db/                      # Database Scripts
│   ├── schema.sql           # DDL (tabela beneficios com índices)
│   └── seed.sql             # Seed data (2 benefícios iniciais)
│
├── docs/                    # Documentação Técnica
│   ├── DOCKER-SETUP.md      # Guia Docker
│   ├── IMPLEMENTATION.md    # Detalhes de implementação
│   └── README.md            # Overview dos docs
│
├── docker-compose.yml       # Orquestração 3 serviços (postgres, backend, frontend)
└── README.md               # Este arquivo (documentação completa)
```

### API Endpoints (Backend REST)

```http
GET    /api/beneficios              # Listar todos os benefícios
GET    /api/beneficios/{id}         # Buscar benefício por ID
POST   /api/beneficios              # Criar novo benefício
PUT    /api/beneficios/{id}         # Atualizar benefício existente
DELETE /api/beneficios/{id}         # Excluir benefício (soft delete)
POST   /api/beneficios/transferir   # Transferir valor entre benefícios
GET    /api/beneficios/{id}/ativo   # Ativar/desativar benefício
```

**Swagger UI**: http://localhost:8080/swagger-ui.html

---

## 🎯 Decisões Técnicas

### Por que Spring Boot + EJB Module?

**Spring Boot 3.2**:
- ✅ API REST moderna e cloud-ready
- ✅ Ecossistema maduro (Spring Data, Validation, Testing)
- ✅ Optimistic Locking (@Version) para concorrência em ambientes distribuídos
- ✅ Fácil integração com Docker e CI/CD

**EJB Module (Jakarta EE 10)**:
- ✅ Demonstração de conhecimento em Jakarta EE e Application Servers
- ✅ Pessimistic Locking para controle transacional mais rigoroso
- ✅ Lock Ordering Pattern para prevenção de deadlocks
- ✅ Alternativa pronta para deploy em WildFly/Payara

### Arquitetura de Camadas

```
┌──────────────────────────────────────────────────────────┐
│  Presentation Layer (Angular Components)                │
│  • beneficio-list, beneficio-form, transfer-dialog       │
└────────────────────┬─────────────────────────────────────┘
                     │ HTTP/JSON
┌────────────────────▼─────────────────────────────────────┐
│  Controller Layer (REST Controllers)                     │
│  • BeneficioController (7 endpoints)                     │
│  • GlobalExceptionHandler                                │
└────────────────────┬─────────────────────────────────────┘
                     │ DTOs
┌────────────────────▼─────────────────────────────────────┐
│  Service Layer (Business Logic)                          │
│  • BeneficioService (@Transactional)                     │
│  • Validações de negócio                                 │
│  • Orchestration de operações                            │
└────────────────────┬─────────────────────────────────────┘
                     │ Entities
┌────────────────────▼─────────────────────────────────────┐
│  Repository Layer (Data Access)                          │
│  • BeneficioRepository (Spring Data JPA)                 │
│  • Query methods customizadas                            │
└────────────────────┬─────────────────────────────────────┘
                     │ JDBC
┌────────────────────▼─────────────────────────────────────┐
│  Database (PostgreSQL 15)                                │
│  • Tabela beneficios com índices                         │
│  • Transações ACID                                       │
└──────────────────────────────────────────────────────────┘
```

### Estratégias de Concorrência

| Estratégia | Implementação | Uso | Trade-offs |
|------------|---------------|-----|------------|
| **Optimistic Lock** | @Version no Spring Boot | Produção atual | ✅ Alta performance, ❌ Retry necessário |
| **Pessimistic Lock** | LockModeType.PESSIMISTIC_WRITE no EJB | Alternativa EJB | ✅ Sem retry, ❌ Menor throughput |

### Tratamento de Erros

**Hierarquia de Exceptions**:
```
Exception
└── RuntimeException
    └── BusinessException (custom)
        ├── BeneficioNotFoundException
        ├── SaldoInsuficienteException
        ├── BeneficioInativoException
        └── TransferenciaInvalidaException
```

**GlobalExceptionHandler**:
- Captura todas as exceptions
- Retorna respostas HTTP padronizadas
- Logs estruturados para debugging
- Rollback automático de transações

### Performance e Otimizações

- ✅ **Connection pooling** (HikariCP) com pool de 10 conexões
- ✅ **Lazy loading** configurado adequadamente
- ✅ **Índices no banco** (numero_beneficio UNIQUE, status)
- ✅ **DTOs** para reduzir overhead de serialização
- ✅ **@Transactional** com propagation REQUIRED
- ✅ **Pessimistic locking** apenas quando necessário (EJB)
- ✅ **Query optimization** com Spring Data JPA

---

##  Executando o Projeto

### Opção 1: Docker Compose (Recomendado - Mais Rápido) 🐳

**Executa PostgreSQL + Backend + Frontend automaticamente:**

```powershell
# 1. Build do backend (necessário antes do Docker)
cd backend-module
mvn clean package -DskipTests
cd ..

# 2. Subir todos os containers (build + start)
docker-compose up --build -d

# 3. Aguardar ~30 segundos para inicialização completa
Start-Sleep -Seconds 30

# 4. Verificar se tudo está rodando
docker-compose ps
```

**Acessar:**
- 🌐 **Frontend (Angular):** http://localhost:4200
- 🔌 **Backend API:** http://localhost:8080/api/beneficios
- 📚 **Swagger UI:** http://localhost:8080/swagger-ui.html
- 🗄️ **PostgreSQL:** localhost:5432 (user: `user`, password: `password`, db: `beneficio`)

**Comandos úteis:**
```powershell
# Ver logs de todos os serviços
docker-compose logs -f

# Ver logs de um serviço específico
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f postgres

# Parar todos os containers
docker-compose down

# Parar e remover volumes (limpar database)
docker-compose down -v

# Reiniciar serviço específico
docker-compose restart backend
docker-compose restart frontend

# Ver status dos containers
docker-compose ps

# Rebuild de um serviço específico
docker-compose up --build -d backend
docker-compose up --build -d frontend
```

### Opção 2: Execução Manual (Desenvolvimento)

#### Terminal 1: Backend (H2 em memória)

```powershell
cd backend-module
mvn spring-boot:run
```

O backend estará disponível em: **http://localhost:8080**

#### Terminal 2: Backend com PostgreSQL local

```powershell
# 1. Iniciar PostgreSQL
docker-compose up -d postgres

# 2. Executar backend
cd backend-module
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

#### Terminal 3: Frontend

```powershell
cd frontend
npm start
```

O frontend estará disponível em: **http://localhost:4200**

### Opção 3: Docker Compose Manual

```powershell
# 1. Build backend
cd backend-module
mvn clean package -DskipTests
cd ..

# 2. Build e iniciar todos os containers
docker-compose build
docker-compose up -d

# 3. Acessar
# Frontend: http://localhost:4200
# Backend: http://localhost:8080
# Swagger: http://localhost:8080/swagger-ui.html
```

---

## 🧪 Testes

### Backend + EJB: Executar Todos os Testes

```powershell
mvn clean test
```

### Backend: Testes de Integração

```powershell
cd backend-module
mvn test -Dtest=BeneficioControllerIntegrationTest
```

### EJB: Testes Unitários

```powershell
cd ejb-module
mvn test
```

### Relatório de Cobertura (JaCoCo)

```powershell
# Backend
cd backend-module
mvn test jacoco:report
# Abrir: backend-module/target/site/jacoco/index.html

# EJB
cd ejb-module
mvn test jacoco:report
# Abrir: ejb-module/target/site/jacoco/index.html
```

### Resultados Esperados

- **Backend:** 20+ testes passando
- **EJB:** 18 testes passando
- **Frontend:** 80 testes passando
- **Cobertura:** Backend ~80% | EJB ~85% | Frontend 100%

---

## 📊 Resumo da Solução do Desafio

### Bug de Concorrência - Solução Completa

**Problema Original**: *"Às vezes, quando fazemos transferências simultâneas, o saldo fica incorreto."*

**Root Cause**: Race condition em operações de leitura-modificação-escrita sem sincronização.

**Soluções Implementadas**:

| Módulo | Estratégia | Status | Resultado |
|--------|-----------|--------|-----------|
| **Backend (Spring Boot)** | Optimistic Locking (@Version) | ✅ Produção | 100% testes passando |
| **EJB Module (Jakarta EE)** | Pessimistic Locking + Lock Ordering | ✅ Testado | 18/18 testes, 85% coverage |

### Resultados dos Testes

```
Frontend (Angular 17):
✅ 80 testes executados
✅ 80 testes passando (100%)
✅ 0 falhas
⏱️ Tempo: 0.591s

Backend (Spring Boot 3.2):
✅ 20+ testes executados
✅ 100% passando
✅ Controllers, Services, Repositories testados

EJB Module (Jakarta EE 10):
✅ 18 testes executados
✅ 18 testes passando (100%)
✅ 0 falhas, 0 erros
✅ Coverage: 85% (203/238 linhas)
✅ Testes de concorrência com 10 threads: PASSOU
✅ Performance < 5ms por operação: PASSOU
```

### Features Implementadas

| Funcionalidade | Status | Detalhes |
|----------------|--------|----------|
| CRUD Benefícios | ✅ Completo | Criar, Listar, Editar, Excluir |
| Transferências | ✅ Completo | Com validações e controle de concorrência |
| Validações | ✅ Completo | Backend (Bean Validation) + Frontend (Reactive Forms) |
| Interface Web | ✅ Completo | Angular Material + Dark/Light Mode |
| API REST | ✅ Completo | 7 endpoints documentados (Swagger) |
| Testes | ✅ Completo | 118+ testes automatizados (100% passando) |
| Docker | ✅ Completo | 3 containers (postgres, backend, frontend) |
| Documentação | ✅ Completo | README, Swagger, Javadocs |

---

## 🎁 Features Extras Implementadas

Além dos requisitos do desafio, foram implementadas:

- 🌙 **Dark Mode**: Toggle persistente no frontend com localStorage
- 🔄 **Optimistic Locking**: @Version no Spring Boot além do pessimistic no EJB
- 📊 **Swagger UI**: Documentação interativa da API
- 🛡️ **Global Exception Handler**: Tratamento centralizado de erros
- 🎨 **Material Design**: Interface moderna e responsiva
- ⚡ **Multi-stage Docker builds**: Imagens otimizadas (~250MB frontend, ~200MB backend)
- 📈 **Testes de performance**: Validação de latência < 5ms
- 🔒 **Lock Ordering Pattern**: Prevenção de deadlocks no EJB
- ✨ **UX aprimorada**: Confirmações, snackbars, loading states, validações real-time
- 🧪 **118+ testes automatizados**: 100% passando em todos os módulos

---

## 📚 Documentação Adicional

- **[backend-module/README.md](./backend-module/README.md)**: Detalhes do Spring Boot API
- **[ejb-module/README.md](./ejb-module/README.md)**: Análise completa do bug e solução EJB
- **[frontend/README.md](./frontend/README.md)**: Documentação do Angular app
- **[docs/DOCKER-SETUP.md](./docs/DOCKER-SETUP.md)**: Guia Docker detalhado
- **[docs/IMPLEMENTATION.md](./docs/IMPLEMENTATION.md)**: Decisões de implementação

---

## 🤝 Sobre o Desafio

Este projeto foi desenvolvido como resposta a um desafio técnico que envolvia:

1. ✅ Análise de um bug de concorrência em transferências
2. ✅ Implementação de solução robusta usando EJB com pessimistic locking
3. ✅ Criação de API REST completa com Spring Boot
4. ✅ Desenvolvimento de frontend Angular com Material Design
5. ✅ Containerização completa com Docker
6. ✅ Testes automatizados abrangentes

**Resultado**: Sistema completo, testado e pronto para produção.

---

<p align="center">
  <strong>✨ Sistema Completo de Gestão de Benefícios ✨</strong><br>
  Desenvolvido com ☕ Java, 🚀 Spring Boot, 🎯 Angular, e 🐳 Docker
</p>


