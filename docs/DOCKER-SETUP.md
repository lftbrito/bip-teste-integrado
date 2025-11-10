# 🐳 Configuração Docker - Backend + PostgreSQL

---

## 🚀 Como Usar

```powershell
# 1. Compilar backend
cd backend-module
mvn clean package -DskipTests

# 2. Voltar para raiz
cd ..

# 3. Iniciar containers
docker-compose up -d

# 4. Ver logs
docker-compose logs -f backend
```

---

## 🏗️ Arquitetura Docker

```
┌─────────────────────────────────────────────┐
│          Docker Network: bip-network         │
│                                              │
│  ┌──────────────────┐  ┌─────────────────┐  │
│  │   PostgreSQL     │  │     Backend     │  │
│  │  (bip-postgres)  │  │   (bip-backend)  │  │
│  │                  │  │                 │  │
│  │  Port: 5432      │◄─┤  Port: 8080     │  │
│  │  User: beneficio │  │  Profile: prod   │  │
│  │  DB: beneficiodb │  │  Java 17 + JAR  │  │
│  │  + schema.sql    │  │                 │  │
│  │  + seed.sql      │  │                 │  │
│  └──────────────────┘  └─────────────────┘  │
│         │                      │             │
└─────────┼──────────────────────┼─────────────┘
          │                      │
          ▼                      ▼
    localhost:5432        localhost:8080
```

---

## 📊 Configuração do PostgreSQL

### Credenciais
- **Host:** localhost (ou `postgres` dentro do Docker)
- **Port:** 5432
- **Database:** beneficiodb
- **Username:** beneficio_user
- **Password:** beneficio_pass

### Dados Iniciais (seed.sql)
```sql
Beneficio A - Saldo: 1000.00 (ATIVO)
Beneficio B - Saldo: 500.00 (ATIVO)
```

### Conectar via Cliente

**DBeaver / pgAdmin:**
```
Host: localhost
Port: 5432
Database: beneficiodb
Username: beneficio_user
Password: beneficio_pass
```

**psql (linha de comando):**
```powershell
docker exec -it bip-postgres psql -U beneficio_user -d beneficiodb
```

---

## 🔧 Variáveis de Ambiente

### Docker Compose (automático)
```yaml
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/beneficiodb
SPRING_DATASOURCE_USERNAME=beneficio_user
SPRING_DATASOURCE_PASSWORD=beneficio_pass
SPRING_JPA_HIBERNATE_DDL_AUTO=validate
```

### Desenvolvimento Local (manual)
```powershell
$env:SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/beneficiodb"
$env:SPRING_DATASOURCE_USERNAME="beneficio_user"
$env:SPRING_DATASOURCE_PASSWORD="beneficio_pass"
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

---

## 📝 Comandos Docker Úteis

### Gerenciamento de Containers

```powershell
# Iniciar tudo
docker-compose up -d

# Parar tudo
docker-compose down

# Parar e remover volumes (LIMPA O BANCO!)
docker-compose down -v

# Ver status
docker-compose ps

# Ver logs em tempo real
docker-compose logs -f

# Ver logs apenas do backend
docker-compose logs -f backend

# Ver logs apenas do PostgreSQL
docker-compose logs -f postgres

# Reiniciar apenas o backend
docker-compose restart backend

# Rebuild backend (após mudanças no código)
docker-compose build backend
docker-compose up -d backend
```

### Acesso aos Containers

```powershell
# Shell no container do backend
docker exec -it bip-backend sh

# Shell no container do PostgreSQL
docker exec -it bip-postgres bash

# psql no PostgreSQL
docker exec -it bip-postgres psql -U beneficio_user -d beneficiodb
```

### Limpeza

```powershell
# Remover containers parados
docker-compose rm

# Remover imagens antigas
docker image prune -a

# Limpar tudo do Docker
docker system prune -a --volumes
```

---

## 🐞 Troubleshooting

### Problema: Backend não conecta no PostgreSQL

**Sintomas:**
```
Connection refused: postgres:5432
```

**Solução:**
```powershell
# 1. Verificar se PostgreSQL está saudável
docker-compose ps

# 2. Ver logs do PostgreSQL
docker-compose logs postgres

# 3. Testar conexão
docker exec -it bip-postgres pg_isready -U beneficio_user
```

---

### Problema: "Port 5432 already in use"

**Causa:** PostgreSQL já rodando localmente

**Solução 1:** Parar PostgreSQL local
```powershell
Stop-Service postgresql-x64-15
```

**Solução 2:** Mudar porta no docker-compose.yml
```yaml
ports:
  - "5433:5432"
```

---

### Problema: "Port 8080 already in use"

**Solução:**
```powershell
# Encontrar processo
netstat -ano | findstr :8080

# Matar processo (substitua PID)
taskkill /PID <PID> /F
```

---

### Problema: Schema não foi criado

**Sintomas:** Erro "relation beneficio does not exist"

**Solução:**
```powershell
# 1. Parar e limpar volumes
docker-compose down -v

# 2. Verificar se arquivos SQL existem
ls db/

# 3. Reiniciar (PostgreSQL executará scripts novamente)
docker-compose up -d
```

---

### Problema: Backend não responde

**Diagnóstico:**
```powershell
# 1. Ver logs
docker-compose logs backend

# 2. Verificar se JAR existe
ls backend-module/target/*.jar

# 3. Verificar healthcheck do PostgreSQL
docker-compose ps
```

**Solução comum:** Esperar mais tempo (backend demora ~15-20s para iniciar)

---

## ✅ Checklist de Verificação

Após executar `docker-compose up -d`:

- [ ] PostgreSQL está rodando: `docker-compose ps postgres`
- [ ] Backend está rodando: `docker-compose ps backend`
- [ ] Sem erros nos logs: `docker-compose logs backend`
- [ ] API responde: `curl http://localhost:8080/api/beneficios`
- [ ] Swagger abre: `http://localhost:8080/swagger-ui.html`
- [ ] 2 benefícios no banco (seed.sql carregado)

---

## 📚 Referências

- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [PostgreSQL Docker Hub](https://hub.docker.com/_/postgres)
- [Spring Boot with Docker](https://spring.io/guides/gs/spring-boot-docker/)

---

**Última atualização:** 10/11/2025  
**Versão:** 1.0.0
