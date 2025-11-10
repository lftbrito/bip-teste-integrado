# Frontend - Sistema de Gestão de Benefícios

> **Versão:** 1.0.0  
> **Framework:** Angular 17.3.17  
> **Node.js:** 20+  
> **UI Library:** Angular Material 17.3.10

## 📋 Visão Geral

Interface web moderna e responsiva para gerenciamento de benefícios de funcionários, desenvolvida com Angular 17, Angular Material Design, e integração completa com API REST backend.

## 🧪 **TESTES IMPLEMENTADOS**

### Cobertura de Testes:
- ✅ `beneficio.service.spec.ts` - 20+ testes do serviço HTTP
- ✅ `beneficio-list.component.spec.ts` - 15+ testes de listagem
- ✅ `beneficio-form.component.spec.ts` - 25+ testes de formulário
- ✅ `transfer-dialog.component.spec.ts` - 20+ testes de transferência
- ✅ `app.component.spec.ts` - 10+ testes do componente raiz

```bash
# Executar todos os testes
npm test

# Executar com cobertura
npm run test:coverage
```

---

## 🚀 Funcionalidades Implementadas

- ✅ **Listagem de Benefícios** com tabela interativa (Material Table)
- ✅ **Criação de Benefícios** com formulário validado
- ✅ **Edição de Benefícios** inline
- ✅ **Exclusão de Benefícios** (soft delete)
- ✅ **Transferências entre Benefícios** com modal de confirmação
- ✅ **Validação de Formulários** em tempo real
- ✅ **Feedback Visual** com snackbars e spinners
- ✅ **Design Responsivo** para desktop e mobile
- ✅ **Tratamento de Erros** com mensagens amigáveis
- ✅ **Integração com Backend** via HTTP Client
- ✅ **Docker Ready** com multi-stage build
- ✅ **Nginx** otimizado para SPA

## 🏗️ Arquitetura

### Estrutura de Diretórios

```
frontend/
├── src/
│   ├── app/
│   │   ├── components/
│   │   │   ├── beneficio-list/          # 📊 Tabela de benefícios
│   │   │   │   ├── beneficio-list.component.ts
│   │   │   │   ├── beneficio-list.component.html
│   │   │   │   └── beneficio-list.component.scss
│   │   │   ├── beneficio-form/          # 📝 Formulário de criação/edição
│   │   │   │   ├── beneficio-form.component.ts
│   │   │   │   ├── beneficio-form.component.html
│   │   │   │   └── beneficio-form.component.scss
│   │   │   └── transfer-dialog/         # 💸 Modal de transferência
│   │   │       ├── transfer-dialog.component.ts
│   │   │       ├── transfer-dialog.component.html
│   │   │       └── transfer-dialog.component.scss
│   │   ├── services/
│   │   │   └── beneficio.service.ts     # 🔌 Service HTTP API
│   │   ├── models/
│   │   │   └── beneficio.model.ts       # 📦 Interfaces TypeScript
│   │   ├── app.component.ts             # 🎯 Componente raiz
│   │   ├── app.config.ts                # ⚙️ Configuração global
│   │   └── app.routes.ts                # 🛣️ Rotas (standalone)
│   ├── index.html                       # 🌐 HTML principal
│   ├── main.ts                          # 🚀 Bootstrap da aplicação
│   └── styles.scss                      # 🎨 Estilos globais
├── Dockerfile                           # 🐳 Multi-stage build
├── nginx.conf                           # ⚙️ Configuração Nginx
├── docker-entrypoint.sh                 # 🔧 Script de inicialização
├── angular.json                         # 📐 Configuração Angular CLI
├── package.json                         # 📦 Dependências NPM
└── tsconfig.json                        # 🔧 Configuração TypeScript
```

### Padrão de Componentes

```
┌─────────────────────────────────┐
│      App Component (Root)       │
└────────────┬────────────────────┘
             │
    ┌────────▼──────────┐
    │  Beneficio List   │ ← Tabela Principal
    └────────┬──────────┘
             │
    ┌────────▼──────────┐
    │  Beneficio Form   │ ← Formulário Create/Update
    └────────┬──────────┘
             │
    ┌────────▼──────────┐
    │ Transfer Dialog   │ ← Modal de Transferência
    └───────────────────┘
```

### Fluxo de Dados

```
Componente → Service → HTTP Client → Backend API
                ↓
          RxJS Observable
                ↓
       Componente (Subscribe)
                ↓
        Atualizar View
```

## 🎨 Componentes Principais

### 1. BeneficioListComponent

**Responsabilidades:**
- Exibir lista de benefícios em Material Table
- Botões de ação (Editar, Excluir, Transferir)
- Filtros e ordenação
- Atualização automática após operações

**Features:**
- Paginação
- Ordenação por colunas
- Formatação de valores monetários
- Status visual (ativo/inativo)

### 2. BeneficioFormComponent

**Responsabilidades:**
- Formulário reativo com validações
- Criação de novos benefícios
- Edição de benefícios existentes
- Validação em tempo real

**Validações:**
- Nome: obrigatório, 3-100 caracteres
- Descrição: opcional, máx 500 caracteres
- Valor: obrigatório, >= 0
- Ativo: obrigatório (checkbox)

### 3. TransferDialogComponent

**Responsabilidades:**
- Modal de transferência entre benefícios
- Seleção de origem e destino
- Validação de valor e saldo
- Confirmação visual com resumo

**Validações:**
- Benefícios diferentes (origem ≠ destino)
- Valor > 0
- Saldo origem >= valor

## 🔌 Service Layer

### BeneficioService

**Métodos Disponíveis:**

```typescript
// Listagem
listarBeneficios(): Observable<Beneficio[]>
listarBeneficiosAtivos(): Observable<Beneficio[]>

// CRUD
buscarPorId(id: number): Observable<Beneficio>
criar(beneficio: Beneficio): Observable<Beneficio>
atualizar(id: number, beneficio: Beneficio): Observable<Beneficio>
excluir(id: number): Observable<void>

// Transferências
transferir(request: TransferenciaRequest): Observable<TransferenciaResponse>
```

**Configuração:**
- Base URL: `http://localhost:8080/api/beneficios`
- Error Handling: Interceptor global
- Retry Logic: 3 tentativas para erros de rede

## 📦 Stack Tecnológica

### Core
- **Angular 17.3.17** - Framework SPA
- **TypeScript 5.4.2** - Linguagem
- **RxJS 7.8.0** - Programação reativa

### UI/UX
- **Angular Material 17.3.10** - Componentes Material Design
- **Angular CDK 17.3.10** - Component Dev Kit
- **SCSS** - Estilização avançada

### Build & Deploy
- **Angular CLI 17.3.17** - Tooling
- **Node.js 20-alpine** - Runtime (Docker)
- **Nginx Alpine** - Web server (produção)

### Testes
- **Jasmine 5.1.0** - Framework de testes
- **Karma 6.4.0** - Test runner

## 🛠️ Como Executar

### Pré-requisitos
- 📦 **Node.js 20+** ([Download](https://nodejs.org/))
- 📦 **npm 10+** (incluído com Node.js)
- ⚙️ **Angular CLI** (opcional): `npm install -g @angular/cli`

### 1. Instalação

```powershell
# Navegar para pasta frontend
cd frontend

# Instalar dependências
npm install
```

### 2. Desenvolvimento Local

```powershell
# Iniciar servidor de desenvolvimento
npm start

# Ou com Angular CLI
ng serve
```

**Aplicação disponível em:** `http://localhost:4200`

**Observação:** O backend deve estar rodando em `http://localhost:8080`

### 3. Build de Produção

```powershell
# Build otimizado
npm run build

# Build com configuração específica
ng build --configuration production
```

**Output:** `dist/frontend/browser/`

### 4. Executar com Docker

#### Opção 1: Docker Compose (Recomendado)

```powershell
# Na raiz do projeto
docker-compose up frontend
```

**Aplicação disponível em:** `http://localhost:4200`

#### Opção 2: Docker standalone

```powershell
# Build da imagem
docker build -t frontend-beneficios:1.0.0 .

# Run container
docker run -p 4200:80 frontend-beneficios:1.0.0
```

#### Opção 3: Docker com variável de ambiente

```powershell
# Alterar URL do backend
docker run -p 4200:80 `
  -e API_URL=http://seu-backend:8080 `
  frontend-beneficios:1.0.0
```

### 5. Acessar Aplicação

Após iniciar:
- **Frontend:** `http://localhost:4200`
- **Backend API:** `http://localhost:8080/api/beneficios`
- **Swagger:** `http://localhost:8080/swagger-ui.html`

## ⚙️ Configuração

### Variáveis de Ambiente

| Variável | Descrição | Padrão | Docker |
|----------|-----------|--------|--------|
| `API_URL` | URL base da API backend | `http://localhost:8080` | Substituído em runtime |

### Configuração do Backend (Service)

Editar `src/app/services/beneficio.service.ts`:

```typescript
private readonly API_URL = 'http://localhost:8080/api/beneficios';
```

### Configuração do Nginx (Produção)

O arquivo `nginx.conf` já está otimizado para:
- ✅ Gzip compression
- ✅ Cache de assets estáticos (1 ano)
- ✅ Roteamento SPA (try_files)
- ✅ Security headers (XSS, Frame Options)

## 🎨 Temas e Estilos

### Angular Material Theme

**Tema:** Indigo-Pink (padrão Angular Material)

### Cores Principais
- **Primary:** Indigo (`#3f51b5`)
- **Accent:** Pink (`#ff4081`)
- **Warn:** Red (`#f44336`)

### Customização

Editar `src/styles.scss`:

```scss
@use '@angular/material' as mat;

$custom-primary: mat.define-palette(mat.$indigo-palette);
$custom-accent: mat.define-palette(mat.$pink-palette);
$custom-warn: mat.define-palette(mat.$red-palette);
```

## 📱 Responsividade

### Breakpoints

| Dispositivo | Largura | Layout |
|-------------|---------|--------|
| Mobile | < 600px | 1 coluna |
| Tablet | 600px - 960px | 2 colunas |
| Desktop | > 960px | 3+ colunas |

### Features Responsivas
- ✅ Tabelas com scroll horizontal em mobile
- ✅ Formulários adaptáveis
- ✅ Dialogs full-screen em mobile
- ✅ Menu hambúrguer em telas pequenas

## 🔒 Validações e Segurança

### Validações de Formulário

#### Benefício
- Nome: obrigatório, min 3, max 100 caracteres
- Descrição: opcional, max 500 caracteres
- Valor: obrigatório, número >= 0
- Ativo: obrigatório, boolean

#### Transferência
- Origem: obrigatório, deve existir
- Destino: obrigatório, deve existir e ser diferente da origem
- Valor: obrigatório, número > 0

### Segurança
- ✅ Sanitização de inputs (Angular built-in)
- ✅ CORS configurado no backend
- ✅ Security headers no Nginx
- ✅ Sem exposição de stack traces

## 🚨 Tratamento de Erros

### Tipos de Erro

#### 1. Erros de Validação (400)
```typescript
{
  status: 400,
  message: "Erro de validação",
  errors: [
    { field: "nome", message: "Nome é obrigatório" }
  ]
}
```

**Ação:** Exibir mensagens no formulário

#### 2. Não Encontrado (404)
```typescript
{
  status: 404,
  message: "Benefício não encontrado"
}
```

**Ação:** Snackbar de erro + recarregar lista

#### 3. Conflito (409)
```typescript
{
  status: 409,
  message: "Saldo insuficiente para transferência"
}
```

**Ação:** Snackbar de erro + manter modal aberto

#### 4. Erro de Servidor (500)
```typescript
{
  status: 500,
  message: "Erro interno do servidor"
}
```

**Ação:** Snackbar de erro genérico

### Feedback Visual

```typescript
// Sucesso
this.snackBar.open('Benefício criado com sucesso!', 'Fechar', {
  duration: 3000,
  panelClass: ['success-snackbar']
});

// Erro
this.snackBar.open('Erro ao criar benefício', 'Fechar', {
  duration: 5000,
  panelClass: ['error-snackbar']
});
```

## 🧪 Testes

### Executar Testes Unitários

```powershell
# Executar todos os testes
npm test

# Ou com Angular CLI
ng test

# Com coverage
ng test --code-coverage
```

### Executar Testes E2E

```powershell
# Configurar E2E (ex: Cypress, Playwright)
ng add @cypress/schematic

# Executar E2E
ng e2e
```

### Estrutura de Testes

```
src/
└── app/
    ├── components/
    │   ├── beneficio-list/
    │   │   └── beneficio-list.component.spec.ts
    │   ├── beneficio-form/
    │   │   └── beneficio-form.component.spec.ts
    │   └── transfer-dialog/
    │       └── transfer-dialog.component.spec.ts
    └── services/
        └── beneficio.service.spec.ts
```

## 🐳 Docker

### Dockerfile (Multi-Stage Build)

**Stage 1: Build**
- Base: `node:20-alpine`
- Instala dependências com `npm ci`
- Executa build de produção

**Stage 2: Serve**
- Base: `nginx:alpine`
- Copia build do Angular
- Configura Nginx para SPA
- Expõe porta 80

### Otimizações Docker

- ✅ Multi-stage build (reduz tamanho final)
- ✅ Alpine Linux (imagens pequenas)
- ✅ npm ci (instalação determinística)
- ✅ Cache de layers otimizado
- ✅ .dockerignore configurado

### Tamanho das Imagens

- **Build Stage:** ~500MB (descartado)
- **Final Image:** ~25MB (nginx + build)

## 📊 Performance

### Build Production

| Métrica | Valor |
|---------|-------|
| Tamanho JS | ~200KB (gzipped) |
| Tamanho CSS | ~50KB (gzipped) |
| First Load | ~1.2s |
| Time to Interactive | ~1.5s |

### Otimizações Aplicadas

- ✅ Lazy Loading de módulos
- ✅ Tree Shaking (Webpack)
- ✅ Minificação de JS/CSS
- ✅ Gzip compression (Nginx)
- ✅ Cache de assets estáticos
- ✅ Preload de recursos críticos

## 🔧 Scripts NPM

| Script | Comando | Descrição |
|--------|---------|-----------|
| `start` | `ng serve` | Dev server (porta 4200) |
| `build` | `ng build` | Build de produção |
| `watch` | `ng build --watch --configuration development` | Build incremental |
| `test` | `ng test` | Executar testes unitários |

## 📚 Recursos Adicionais

### Documentação
- [Angular Documentation](https://angular.dev/)
- [Angular Material](https://material.angular.io/)
- [RxJS Documentation](https://rxjs.dev/)
- [TypeScript Handbook](https://www.typescriptlang.org/docs/)

### Ferramentas Recomendadas
- [VS Code](https://code.visualstudio.com/) - IDE recomendada
- [Angular DevTools](https://angular.io/guide/devtools) - Extensão Chrome
- [Augury](https://augury.rangle.io/) - Debug Angular

### Extensões VS Code
- Angular Language Service
- Angular Snippets
- ESLint
- Prettier
- Auto Import

## 🐛 Troubleshooting

### Problema: Porta 4200 já em uso

**Erro:**
```
Port 4200 is already in use.
```

**Solução:**
```powershell
# Usar outra porta
ng serve --port 4300

# Ou matar processo
Get-Process -Id (Get-NetTCPConnection -LocalPort 4200).OwningProcess | Stop-Process -Force
```

---

### Problema: Erro de conexão com backend

**Erro:**
```
Http failure response for http://localhost:8080/api/beneficios: 0 Unknown Error
```

**Soluções:**
1. Verificar se backend está rodando: `http://localhost:8080/api/beneficios`
2. Verificar CORS no backend (`CorsConfig.java`)
3. Verificar URL no `beneficio.service.ts`

---

### Problema: npm install falha

**Erro:**
```
npm ERR! code ERESOLVE
```

**Solução:**
```powershell
# Limpar cache
npm cache clean --force

# Deletar node_modules e package-lock.json
Remove-Item -Recurse -Force node_modules
Remove-Item package-lock.json

# Reinstalar
npm install
```

---

### Problema: Build de produção falha

**Solução:**
```powershell
# Limpar e rebuildar
ng cache clean
ng build --configuration production
```

---

### Problema: Erros de TypeScript

**Solução:**
```powershell
# Verificar versão do TypeScript
npm list typescript

# Reinstalar dependências
npm install
```

## 🚀 Deploy em Produção

### Checklist de Produção

- [ ] Build de produção executado
- [ ] Testes passando
- [ ] Variáveis de ambiente configuradas
- [ ] Backend acessível
- [ ] CORS configurado
- [ ] HTTPS habilitado (recomendado)
- [ ] Logs de erro monitorados
- [ ] Performance testada

### Deploy com Docker

```powershell
# 1. Build da imagem
docker build -t frontend-beneficios:1.0.0 .

# 2. Tag para registry
docker tag frontend-beneficios:1.0.0 seu-registry/frontend-beneficios:1.0.0

# 3. Push para registry
docker push seu-registry/frontend-beneficios:1.0.0

# 4. Deploy no servidor
docker run -d -p 80:80 `
  -e API_URL=https://api.producao.com `
  --name frontend `
  seu-registry/frontend-beneficios:1.0.0
```

## 📝 Changelog

### v1.0.0 (2025-11-10)
- ✅ Interface completa de gerenciamento de benefícios
- ✅ CRUD completo com validações
- ✅ Transferências entre benefícios
- ✅ Integração com backend REST API
- ✅ Angular Material Design
- ✅ Design responsivo
- ✅ Docker multi-stage build
- ✅ Nginx otimizado para SPA

---

**Última atualização:** 10/11/2025  
**Versão:** 1.0.0  
**Status:** ✅ Produção-ready
