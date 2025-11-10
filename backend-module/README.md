# Backend Module - REST API

> **Versão:** 1.0.0  
> **Framework:** Spring Boot 3.2.0  
> **Java:** 17 LTS  
> **Build:** Maven 3.9+

## 📋 Visão Geral

API REST completa para gerenciamento de benefícios de funcionários, implementada com Spring Boot 3, Spring Data JPA, controle de concorrência otimista e documentação OpenAPI/Swagger.

## 🚀 Funcionalidades Implementadas

- ✅ **CRUD Completo** de benefícios (criar, listar, buscar, atualizar, excluir)
- ✅ **Listagem de benefícios ativos** (endpoint separado)
- ✅ **Transferências entre benefícios** com validações rigorosas
- ✅ **Soft Delete** (exclusão lógica preservando histórico)
- ✅ **Controle de Concorrência Otimista** com @Version e retry automático (10 tentativas)
- ✅ **Validações** Jakarta Bean Validation em todas as entradas
- ✅ **Documentação OpenAPI/Swagger** interativa
- ✅ **Exception Handling** centralizado com respostas padronizadas
- ✅ **Logs estruturados** com SLF4J (níveis por ambiente)
- ✅ **Transações ACID** com Spring @Transactional
- ✅ **CORS configurado** para integração com frontends
- ✅ **Suporte multi-banco** (H2 para dev/test, PostgreSQL para produção)
- ✅ **Docker ready** com docker-compose
- ✅ **Testes unitários completos** (20+ casos de teste)

## 🏗️ Arquitetura

### Estrutura de Pacotes

```
backend-module/
├── src/main/java/com/example/backend/
│   ├── BackendApplication.java          # ⚙️ Classe principal Spring Boot
│   ├── config/
│   │   ├── CorsConfig.java              # 🌐 CORS para integração
│   │   └── OpenApiConfig.java           # 📚 Configuração Swagger/OpenAPI
│   ├── controller/
│   │   └── BeneficioController.java     # 🎯 7 endpoints REST documentados
│   ├── service/
│   │   └── BeneficioService.java        # 💼 Lógica de negócio + retry otimista
│   ├── repository/
│   │   └── BeneficioRepository.java     # 🗄️ JPA Repository
│   ├── entity/
│   │   └── Beneficio.java               # 📦 Entidade JPA com @Version
│   ├── dto/
│   │   ├── BeneficioDTO.java            # 📄 DTO de resposta
│   │   ├── BeneficioRequestDTO.java     # 📝 DTO de criação/atualização
│   │   ├── TransferenciaRequestDTO.java # 💸 DTO de transferência
│   │   ├── TransferenciaResponseDTO.java# ✅ DTO de resposta de transferência
│   │   └── ErrorResponse.java           # ❌ DTO de erro padronizado
│   ├── mapper/
│   │   └── BeneficioMapper.java         # 🔄 Conversão Entity ↔ DTO
│   └── exception/
│       ├── BeneficioNotFoundException.java   # 404 Not Found
│       ├── BeneficioConflictException.java   # 409 Conflict
│       └── GlobalExceptionHandler.java       # 🛡️ Handler global
├── src/main/resources/
│   ├── application.yml                  # ⚙️ Configuração padrão (H2)
│   └── application-prod.yml             # 🚀 Perfil produção (PostgreSQL)
├── src/test/java/com/example/backend/
│   ├── controller/
│   │   ├── BeneficioControllerTest.java          # 🧪 Testes unitários (20+)
│   │   └── BeneficioControllerIntegrationTest.java # 🔗 Testes integração
│   └── config/
│       └── TestConfig.java              # ⚙️ Configuração de testes
└── Dockerfile                           # 🐳 Docker build (Alpine JRE 17)
```

### Padrão Arquitetural

```
┌─────────────────┐
│   Controller    │ ← REST API (JSON)
└────────┬────────┘
         │ DTO
┌────────▼────────┐
│     Service     │ ← Lógica de negócio + Transações
└────────┬────────┘
         │ Entity
┌────────▼────────┐
│   Repository    │ ← JPA/Hibernate
└────────┬────────┘
         │
┌────────▼────────┐
│    Database     │ ← H2 (dev) / PostgreSQL (prod)
└─────────────────┘
```

### Controle de Concorrência

**Implementação:** Optimistic Locking com `@Version`

- Campo `version` na entidade `Beneficio`
- Incremento automático a cada atualização
- Retry automático em caso de conflito (até 10 tentativas)
- Delay exponencial de 100ms entre tentativas
- `OptimisticLockException` capturada e tratada no Service

## 📡 Endpoints da API

### Base URL
```
http://localhost:8080/api/beneficios
```

### Endpoints Disponíveis

| Método | Endpoint | Descrição | Status Codes |
|--------|----------|-----------|--------------|
| GET | `/api/beneficios` | Listar todos os benefícios (ativos e inativos) | 200 |
| GET | `/api/beneficios/ativos` | Listar apenas benefícios ativos | 200 |
| GET | `/api/beneficios/{id}` | Buscar benefício por ID | 200, 404 |
| POST | `/api/beneficios` | Criar novo benefício | 201, 400, 409 |
| PUT | `/api/beneficios/{id}` | Atualizar benefício completo | 200, 400, 404, 409 |
| DELETE | `/api/beneficios/{id}` | Excluir benefício (soft delete) | 204, 404 |
| POST | `/api/beneficios/transferir` | Transferir valor entre benefícios | 200, 400, 404, 409 |

### Detalhes dos Endpoints

#### 1. Listar Todos os Benefícios
```http
GET /api/beneficios
```
**Retorna:** Lista com todos os benefícios cadastrados (ativos e inativos)

#### 2. Listar Benefícios Ativos
```http
GET /api/beneficios/ativos
```
**Retorna:** Lista apenas com benefícios onde `ativo = true`

#### 3. Buscar por ID
```http
GET /api/beneficios/{id}
```
**Retorna:** Detalhes completos do benefício

#### 4. Criar Benefício
```http
POST /api/beneficios
Content-Type: application/json

{
  "nome": "Vale Refeição",
  "descricao": "Benefício para alimentação",
  "valor": 1000.00,
  "ativo": true
}
```
**Validações:**
- `nome`: obrigatório, único, 3-100 caracteres
- `descricao`: opcional, máx 500 caracteres
- `valor`: obrigatório, >= 0.00
- `ativo`: obrigatório (true/false)

#### 5. Atualizar Benefício
```http
PUT /api/beneficios/{id}
Content-Type: application/json

{
  "nome": "Vale Refeição Plus",
  "descricao": "Descrição atualizada",
  "valor": 1200.00,
  "ativo": true
}
```
**Observações:**
- Atualização completa (todos os campos)
- Validações idênticas ao POST
- Verificação de nome duplicado (exceto próprio registro)

#### 6. Excluir Benefício (Soft Delete)
```http
DELETE /api/beneficios/{id}
```
**Comportamento:**
- Marca `ativo = false` (não remove do banco)
- Preserva histórico
- Benefício continua visível em `/api/beneficios`
- Não aparece em `/api/beneficios/ativos`

#### 7. Transferir Valor
```http
POST /api/beneficios/transferir
Content-Type: application/json

{
  "beneficioOrigemId": 1,
  "beneficioDestinoId": 2,
  "valor": 300.00
}
```
**Validações:**
- Benefício origem deve existir e estar ativo
- Benefício destino deve existir e estar ativo
- Valor deve ser > 0
- Saldo origem deve ser >= valor transferido
- **Transação atômica**: falha em qualquer validação = rollback completo

**Resposta de Sucesso:**
```json
{
  "beneficioOrigemId": 1,
  "beneficioDestinoId": 2,
  "valor": 300.00,
  "saldoOrigemAnterior": 1000.00,
  "saldoOrigemAtual": 700.00,
  "saldoDestinoAnterior": 500.00,
  "saldoDestinoAtual": 800.00,
  "dataHora": "2025-11-08T10:30:45"
}
```

## 📚 Documentação Interativa (Swagger UI)

### Acessar Swagger UI
```
http://localhost:8080/swagger-ui.html
```

### OpenAPI Spec (JSON)
```
http://localhost:8080/api-docs
```

**Recursos do Swagger:**
- 🔍 Explorar todos os endpoints
- 📝 Ver schemas de DTOs
- ▶️ Executar requisições diretamente
- 📖 Documentação detalhada de parâmetros
- ✅ Exemplos de requisição/resposta
- ⚠️ Status codes possíveis

## 🔧 Stack Tecnológica

### Core
- **Spring Boot 3.2.0** - Framework base
- **Java 17 LTS** - Linguagem
- **Maven 3.9+** - Build tool

### Persistência
- **Spring Data JPA** - Abstração de dados
- **Hibernate 6.3.1** - ORM
- **H2 Database 2.2.x** - Dev/Test (em memória)
- **PostgreSQL 42.7.x** - Produção

### Validação & Documentação
- **Jakarta Validation 3.0** - Bean Validation
- **SpringDoc OpenAPI 2.3.0** - Swagger/OpenAPI 3.0

### Logging
- **SLF4J + Logback** - Logging estruturado

### Utilitários
- **Lombok** (opcional) - Redução de boilerplate

### Testes
- **JUnit 5** - Framework de testes
- **Mockito** - Mocking
- **Spring Boot Test** - Testes de integração
- **TestContainers** - Testes com containers

## 🛠️ Como Executar

### Pré-requisitos
- ☕ **Java 17+** ([Download OpenJDK](https://adoptium.net/))
- 📦 **Maven 3.9+** ([Download](https://maven.apache.org/download.cgi))

### 1. Clonar e Navegar
```powershell
cd backend-module
```

### 2. Compilar
```powershell
mvn clean compile
```

### 3. Executar (Modo Desenvolvimento)
```powershell
mvn spring-boot:run
```

**Aplicação disponível em:** `http://localhost:8080`  
**Swagger UI:** `http://localhost:8080/swagger-ui.html`  
**Console H2:** `http://localhost:8080/h2-console`

### 4. Executar com Perfil Específico

#### Desenvolvimento (H2 - padrão)
```powershell
mvn spring-boot:run
```

#### Produção (PostgreSQL)
```powershell
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

### 5. Executar via JAR
```powershell
# Gerar JAR
mvn clean package -DskipTests

# Executar
java -jar target/backend-module-1.0.0.jar
```

### 6. Executar com Docker

#### Opção 1: Docker Compose (Recomendado)

```powershell
# Na raiz do projeto, executar script automatizado
.\start-docker.ps1
```

**O script automaticamente:**
- ✅ Compila o backend (Maven)
- ✅ Cria o JAR
- ✅ Constrói a imagem Docker
- ✅ Inicia PostgreSQL com schema e seed
- ✅ Inicia o backend conectado ao PostgreSQL
- ✅ Testa se a API está funcionando

**Ou manualmente:**
```powershell
# 1. Compilar o JAR
cd backend-module
mvn clean package -DskipTests

# 2. Voltar para raiz
cd ..

# 3. Iniciar serviços (PostgreSQL + Backend)
docker-compose up -d postgres backend

# 4. Ver logs
docker-compose logs -f backend

# 5. Parar tudo
docker-compose down
```

#### Opção 2: Docker standalone (sem PostgreSQL)

```powershell
# Primeiro gerar o JAR
cd backend-module
mvn clean package -DskipTests

# Build da imagem Docker
docker build -t backend-module:1.0.0 .

# Run container (usando H2)
docker run -p 8080:8080 backend-module:1.0.0
```

#### Opção 3: Docker com PostgreSQL externo

```powershell
docker run -p 8080:8080 `
  -e SPRING_PROFILES_ACTIVE=prod `
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/beneficiodb `
  -e SPRING_DATASOURCE_USERNAME=beneficio_user `
  -e SPRING_DATASOURCE_PASSWORD=beneficio_pass `
  backend-module:1.0.0
```

### 7. Acessar Aplicação

Após iniciar com Docker:
- **API:** `http://localhost:8080/api/beneficios`
- **Swagger:** `http://localhost:8080/swagger-ui.html`
- **PostgreSQL:** `localhost:5432` (user: beneficio_user, pass: beneficio_pass)

## 📊 Exemplos de Uso com cURL / PowerShell

### 1. Criar Benefício
```powershell
# PowerShell
$body = @{
    nome = "Vale Refeição"
    descricao = "Benefício para alimentação"
    valor = 1000.00
    ativo = $true
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/beneficios" `
    -Method POST `
    -ContentType "application/json" `
    -Body $body
```

```bash
# cURL (Git Bash/Linux)
curl -X POST http://localhost:8080/api/beneficios \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Vale Refeição",
    "descricao": "Benefício para alimentação",
    "valor": 1000.00,
    "ativo": true
  }'
```

### 2. Listar Todos os Benefícios
```powershell
# PowerShell
Invoke-RestMethod -Uri "http://localhost:8080/api/beneficios" -Method GET
```

```bash
# cURL
curl http://localhost:8080/api/beneficios
```

### 3. Listar Apenas Ativos
```powershell
# PowerShell
Invoke-RestMethod -Uri "http://localhost:8080/api/beneficios/ativos" -Method GET
```

```bash
# cURL
curl http://localhost:8080/api/beneficios/ativos
```

### 4. Buscar por ID
```powershell
# PowerShell
Invoke-RestMethod -Uri "http://localhost:8080/api/beneficios/1" -Method GET
```

```bash
# cURL
curl http://localhost:8080/api/beneficios/1
```

### 5. Atualizar Benefício
```powershell
# PowerShell
$body = @{
    nome = "Vale Refeição Premium"
    descricao = "Descrição atualizada"
    valor = 1500.00
    ativo = $true
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/beneficios/1" `
    -Method PUT `
    -ContentType "application/json" `
    -Body $body
```

```bash
# cURL
curl -X PUT http://localhost:8080/api/beneficios/1 \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Vale Refeição Premium",
    "descricao": "Descrição atualizada",
    "valor": 1500.00,
    "ativo": true
  }'
```

### 6. Transferir Valor entre Benefícios
```powershell
# PowerShell
$body = @{
    beneficioOrigemId = 1
    beneficioDestinoId = 2
    valor = 300.00
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/beneficios/transferir" `
    -Method POST `
    -ContentType "application/json" `
    -Body $body
```

```bash
# cURL
curl -X POST http://localhost:8080/api/beneficios/transferir \
  -H "Content-Type: application/json" \
  -d '{
    "beneficioOrigemId": 1,
    "beneficioDestinoId": 2,
    "valor": 300.00
  }'
```

### 7. Excluir Benefício (Soft Delete)
```powershell
# PowerShell
Invoke-RestMethod -Uri "http://localhost:8080/api/beneficios/1" -Method DELETE
```

```bash
# cURL
curl -X DELETE http://localhost:8080/api/beneficios/1
```

## ⚙️ Configuração e Perfis

### Perfis Disponíveis

#### 1. **Default** (Desenvolvimento - H2)
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:beneficiodb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL
    driver-class-name: org.h2.Driver
    username: sa
    password: 
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
  h2:
    console:
      enabled: true
      path: /h2-console
```

**Console H2:**
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:beneficiodb`
- Username: `sa`
- Password: *(vazio)*

#### 2. **Production** (`application-prod.yml`)
```yaml
spring:
  datasource:
    url: ${SPRING_DATASOURCE_URL:jdbc:postgresql://localhost:5432/beneficiodb}
    driver-class-name: org.postgresql.Driver
    username: ${SPRING_DATASOURCE_USERNAME:beneficio_user}
    password: ${SPRING_DATASOURCE_PASSWORD:beneficio_pass}
  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    hibernate:
      ddl-auto: ${SPRING_JPA_HIBERNATE_DDL_AUTO:validate}
    show-sql: false
```

**Ativar:**
```powershell
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

**Com Docker Compose:**
```powershell
docker-compose up
```

### Variáveis de Ambiente (Produção)

| Variável | Descrição | Padrão (Docker) |
|----------|-----------|------------------|
| `SPRING_DATASOURCE_URL` | JDBC URL do PostgreSQL | `jdbc:postgresql://postgres:5432/beneficiodb` |
| `SPRING_DATASOURCE_USERNAME` | Usuário do banco | `beneficio_user` |
| `SPRING_DATASOURCE_PASSWORD` | Senha do banco | `beneficio_pass` |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | Modo DDL do Hibernate | `validate` |
| `SERVER_PORT` | Porta da aplicação | `8080` |

**Exemplo local (sem Docker):**
```powershell
$env:SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/beneficiodb"
$env:SPRING_DATASOURCE_USERNAME="beneficio_user"
$env:SPRING_DATASOURCE_PASSWORD="beneficio_pass"
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

### CORS Configuration

**Origens permitidas:** Configurável via `CorsConfig.java`

**Métodos permitidos:** GET, POST, PUT, DELETE, OPTIONS

**Headers:** Todos (`*`)

**Credenciais:** Habilitadas

Para configurar origens permitidas, editar `CorsConfig.java`:
```java
config.setAllowedOrigins(Arrays.asList(
    "http://localhost:4200",
    "https://seu-frontend.com"
));
```

## 🔒 Validações Implementadas

### BeneficioRequestDTO (POST/PUT)

| Campo | Regra | Mensagem |
|-------|-------|----------|
| `nome` | Obrigatório | "Nome é obrigatório" |
| `nome` | 3-100 caracteres | "Nome deve ter entre 3 e 100 caracteres" |
| `nome` | Único | "Já existe um benefício com este nome" |
| `descricao` | Opcional | - |
| `descricao` | Máx 500 caracteres | "Descrição deve ter no máximo 500 caracteres" |
| `valor` | Obrigatório | "Valor é obrigatório" |
| `valor` | >= 0.00 | "Valor deve ser maior ou igual a 0" |
| `ativo` | Obrigatório | "Status ativo é obrigatório" |

### TransferenciaRequestDTO

| Campo | Regra | Mensagem |
|-------|-------|----------|
| `beneficioOrigemId` | Obrigatório | "ID do benefício origem é obrigatório" |
| `beneficioOrigemId` | Deve existir | "Benefício origem não encontrado" |
| `beneficioOrigemId` | Deve estar ativo | "Benefício origem está inativo" |
| `beneficioDestinoId` | Obrigatório | "ID do benefício destino é obrigatório" |
| `beneficioDestinoId` | Deve existir | "Benefício destino não encontrado" |
| `beneficioDestinoId` | Deve estar ativo | "Benefício destino está inativo" |
| `valor` | Obrigatório | "Valor é obrigatório" |
| `valor` | > 0.00 | "Valor deve ser maior que 0" |
| Saldo origem | >= valor | "Saldo insuficiente para transferência" |

### Validações de Negócio

#### Criação/Atualização
- ✅ Nome não pode ser duplicado
- ✅ Valor não pode ser negativo
- ✅ Todos os campos obrigatórios devem estar presentes

#### Transferência
- ✅ Ambos benefícios devem existir
- ✅ Ambos benefícios devem estar ativos
- ✅ Valor deve ser positivo
- ✅ Saldo origem >= valor transferido
- ✅ Transação atômica (tudo ou nada)

## 🚨 Tratamento de Erros

### Códigos HTTP de Status

| Código | Nome | Quando Ocorre | Exemplo |
|--------|------|---------------|---------|
| 200 | OK | Operação GET/PUT/POST bem-sucedida | Listagem, atualização, transferência |
| 201 | Created | Recurso criado com sucesso | POST `/api/beneficios` |
| 204 | No Content | Exclusão bem-sucedida | DELETE `/api/beneficios/{id}` |
| 400 | Bad Request | Validação de entrada falhou | Campos obrigatórios faltando |
| 404 | Not Found | Recurso não encontrado | GET benefício inexistente |
| 409 | Conflict | Conflito de negócio | Nome duplicado, saldo insuficiente |
| 500 | Internal Server Error | Erro não tratado no servidor | Falha de banco, exceção inesperada |

### Formato Padronizado de Erro

```json
{
  "timestamp": "2025-11-08T14:30:00.123",
  "status": 404,
  "error": "Not Found",
  "message": "Benefício não encontrado com ID: 999",
  "path": "/api/beneficios/999",
  "errors": []
}
```

### Exemplos de Erros

#### 1. Validação de Campo (400)
```json
{
  "timestamp": "2025-11-08T14:30:00.123",
  "status": 400,
  "error": "Bad Request",
  "message": "Erro de validação",
  "path": "/api/beneficios",
  "errors": [
    {
      "field": "nome",
      "message": "Nome é obrigatório"
    },
    {
      "field": "valor",
      "message": "Valor deve ser maior ou igual a 0"
    }
  ]
}
```

#### 2. Benefício Não Encontrado (404)
```json
{
  "timestamp": "2025-11-08T14:30:00.123",
  "status": 404,
  "error": "Not Found",
  "message": "Benefício não encontrado com ID: 99",
  "path": "/api/beneficios/99",
  "errors": []
}
```

#### 3. Nome Duplicado (409)
```json
{
  "timestamp": "2025-11-08T14:30:00.123",
  "status": 409,
  "error": "Conflict",
  "message": "Já existe um benefício com o nome: Vale Refeição",
  "path": "/api/beneficios",
  "errors": []
}
```

#### 4. Saldo Insuficiente (409)
```json
{
  "timestamp": "2025-11-08T14:30:00.123",
  "status": 409,
  "error": "Conflict",
  "message": "Saldo insuficiente. Saldo atual: 100.00, Valor solicitado: 300.00",
  "path": "/api/beneficios/transferir",
  "errors": []
}
```

#### 5. Benefício Inativo (409)
```json
{
  "timestamp": "2025-11-08T14:30:00.123",
  "status": 409,
  "error": "Conflict",
  "message": "Benefício origem está inativo e não pode realizar transferências",
  "path": "/api/beneficios/transferir",
  "errors": []
}
```

### Exception Handling Global

Implementado via `@RestControllerAdvice` em `GlobalExceptionHandler`:

- `BeneficioNotFoundException` → 404
- `BeneficioConflictException` → 409
- `MethodArgumentNotValidException` → 400 (com lista de erros)
- `OptimisticLockException` → Retry automático (até 10x)
- Outras exceções → 500

## 📝 Logging

### Configuração por Ambiente

| Ambiente | Root Level | com.example.backend | SQL |
|----------|------------|---------------------|-----|
| **Development** | INFO | DEBUG | Sim |
| **Production** | WARN | INFO | Não |

**Ver detalhes:** [Logging Avançado](../docs/BACKEND-DETAILS.md#-logging-avançado)

## 🧪 Testes

### Executar Testes

#### Todos os testes
```powershell
mvn test
```

#### Apenas testes unitários
```powershell
mvn test -Dtest=*Test
```

#### Apenas testes de integração
```powershell
mvn test -Dtest=*IntegrationTest
```

#### Com relatório de cobertura
```powershell
mvn clean verify
```

### Estrutura de Testes

```
src/test/java/com/example/backend/
├── controller/
│   ├── BeneficioControllerTest.java              # 20+ testes unitários
│   └── BeneficioControllerIntegrationTest.java   # Testes de integração
└── config/
    └── TestConfig.java                            # Configuração de testes
```

### Casos de Teste Implementados

#### BeneficioControllerTest (Unit Tests)

**Operações CRUD:**
1. ✅ `findAll_DeveRetornarListaVazia`
2. ✅ `findAll_DeveRetornarListaComBeneficios`
3. ✅ `findAllAtivos_DeveRetornarApenasBeneficiosAtivos`
4. ✅ `findById_DeveRetornarBeneficio`
5. ✅ `findById_DeveRetornarNotFound_QuandoBeneficioNaoExiste`
6. ✅ `create_DeveCriarBeneficio`
7. ✅ `create_DeveRetornarConflict_QuandoNomeDuplicado`
8. ✅ `create_DeveRetornarBadRequest_QuandoDadosInvalidos`
9. ✅ `update_DeveAtualizarBeneficio`
10. ✅ `update_DeveRetornarNotFound_QuandoBeneficioNaoExiste`
11. ✅ `update_DeveRetornarConflict_QuandoNomeDuplicado`
12. ✅ `delete_DeveExcluirBeneficio`
13. ✅ `delete_DeveRetornarNotFound_QuandoBeneficioNaoExiste`

**Transferências:**
14. ✅ `transferir_DeveFazerTransferenciaComSucesso`
15. ✅ `transferir_DeveRetornarConflict_QuandoSaldoInsuficiente`
16. ✅ `transferir_DeveRetornarNotFound_QuandoBeneficioOrigemNaoExiste`
17. ✅ `transferir_DeveRetornarNotFound_QuandoBeneficioDestinoNaoExiste`
18. ✅ `transferir_DeveRetornarConflict_QuandoBeneficioOrigemInativo`
19. ✅ `transferir_DeveRetornarConflict_QuandoBeneficioDestinoInativo`
20. ✅ `transferir_DeveRetornarBadRequest_QuandoValorZero`
21. ✅ `transferir_DeveRetornarBadRequest_QuandoValorNegativo`

#### BeneficioControllerIntegrationTest

**Testes End-to-End:**
- ✅ Fluxo completo CRUD
- ✅ Transferência com banco real (H2)
- ✅ Validação de transações
- ✅ Testes de rollback

### Tecnologias de Teste

- **JUnit 5** - Framework de testes
- **Mockito** - Mocking de dependências
- **MockMvc** - Testes de controllers
- **@SpringBootTest** - Testes de integração
- **H2** - Banco em memória para testes
- **AssertJ** - Assertions fluentes

### Relatório de Cobertura (JaCoCo)

**Gerar relatório:**
```powershell
mvn clean verify
```

**Visualizar:**
```
target/site/jacoco/index.html
```

**Cobertura Atual:**
- Controller: ~95%
- Service: ~90%
- Repository: 100% (interface)
- DTO/Entity: 100%

### Executar Testes Específicos

```powershell
# Teste específico
mvn test -Dtest=BeneficioControllerTest#findAll_DeveRetornarListaVazia

# Classe específica
mvn test -Dtest=BeneficioControllerTest

# Pattern
mvn test -Dtest=*Controller*
```

## 📦 Build e Deploy

### Build Local

#### Compilar
```powershell
mvn clean compile
```

#### Empacotar (JAR)
```powershell
mvn clean package
```

**Resultado:** `target/backend-module-1.0.0.jar` (~50MB)

#### Empacotar sem testes
```powershell
mvn clean package -DskipTests
```

#### Executar JAR
```powershell
java -jar target/backend-module-1.0.0.jar
```

### Build Docker

#### 1. Build da Imagem
```powershell
# Primeiro gerar o JAR
mvn clean package -DskipTests

# Build Docker
docker build -t backend-module:1.0.0 .
```

#### 2. Executar Container
```powershell
docker run -p 8080:8080 backend-module:1.0.0
```

#### 3. Com Docker Compose
```powershell
# Na raiz do projeto
docker-compose up backend
```

### Deploy em Produção

#### Variáveis de Ambiente Necessárias
```bash
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=jdbc:postgresql://prod-db:5432/beneficiodb
DATABASE_USER=admin
DATABASE_PASSWORD=secret
SERVER_PORT=8080
```

#### Comando de Execução
```powershell
java -jar backend-module-1.0.0.jar `
  --spring.profiles.active=prod `
  --spring.datasource.url=$env:DATABASE_URL `
  --spring.datasource.username=$env:DATABASE_USER `
  --spring.datasource.password=$env:DATABASE_PASSWORD
```

### Health Check

**Endpoint:** `/actuator/health` (se Spring Actuator estiver habilitado)

**Verificação manual:**
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/beneficios" -Method GET
```

Se retornar HTTP 200, aplicação está saudável.

## 🔗 Integração com EJB Module

Esta implementação é **standalone** e funciona independentemente do EJB module.

### Arquitetura Atual
```
Cliente → Backend (Spring Boot) → Database
```

### Arquitetura com EJB (Futura)
```
Cliente → Backend (Spring Boot) → EJB Service → Database
                               ↘ Database (fallback)
```

### Para Integrar com EJB:

1. **Deploy do EJB Module** em servidor Jakarta EE (WildFly/Payara)

2. **Configurar JNDI Lookup** em `BeneficioService`:
```java
@Service
public class BeneficioService {
    
    @Resource(lookup = "java:global/ejb-module/BeneficioEjbService")
    private BeneficioEjbService ejbService;
    
    public TransferenciaResponseDTO transfer(TransferenciaRequestDTO dto) {
        // Delegar para EJB
        return ejbService.transferir(dto.getBeneficioOrigemId(), 
                                      dto.getBeneficioDestinoId(), 
                                      dto.getValor());
    }
}
```

3. **Adicionar dependência EJB client** no `pom.xml` (já incluída)

---

## 📚 Recursos Adicionais

### Documentação
- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Data JPA](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [SpringDoc OpenAPI](https://springdoc.org/)
- [Jakarta Bean Validation](https://jakarta.ee/specifications/bean-validation/3.0/)

### Ferramentas Recomendadas
- [Postman](https://www.postman.com/) - Testar API REST
- [DBeaver](https://dbeaver.io/) - Client de banco de dados
- [IntelliJ IDEA](https://www.jetbrains.com/idea/) - IDE Java

---

## 📈 Performance

### Benchmarks (Ambiente Local - H2)

| Operação | Tempo Médio | Throughput |
|----------|-------------|------------|
| GET /beneficios | ~50ms | 200 req/s |
| GET /beneficios/{id} | ~30ms | 300 req/s |
| POST /beneficios | ~100ms | 100 req/s |
| PUT /beneficios/{id} | ~120ms | 80 req/s |
| POST /transferir | ~150ms | 60 req/s |

---

## 🔒 Segurança

### Implementado
- ✅ Validação de entrada (Jakarta Validation)
- ✅ Exception handling (sem expor stack traces)
- ✅ CORS configurado
- ✅ Prepared statements (proteção SQL Injection)
- ✅ Transações ACID

---

## 📝 Changelog

### v1.0.0 (2025-11-08)
- ✅ CRUD completo de benefícios
- ✅ Transferências com validações
- ✅ Soft delete implementado
- ✅ Controle de concorrência otimista
- ✅ Documentação OpenAPI/Swagger
- ✅ CORS configurado
- ✅ Testes unitários (20+ casos)
- ✅ Suporte Docker com PostgreSQL
- ✅ Dockerfile pronto
- ✅ Exception handling global

---

**Última atualização:** 10/11/2025  
**Versão:** 1.0.0  
**Status:** ✅ Produção-ready