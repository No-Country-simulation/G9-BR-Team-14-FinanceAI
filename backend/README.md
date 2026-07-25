# Finance AI Backend API

API REST em **Java + Spring Boot** que fornece o backend do projeto **Finance AI**.

---

## 📌 Sumário

- [Visão geral](#-visão-geral)
- [Stack tecnológica](#-stack-tecnológica)
- [Arquitetura e organização](#-arquitetura-e-organização)
- [Pré-requisitos](#-pré-requisitos)
- [Configuração de ambiente](#-configuração-de-ambiente)
- [Como executar](#-como-executar)
- [Testes](#-testes)
- [Documentação da API (Swagger)](#-documentação-da-api-swagger)
- [Usuário de testes](#-usuário-de-testes)
- [Observações importantes](#-observações-importantes)
- [Fluxo de contribuição (Git)](#-fluxo-de-contribuição-git)

---

## 🎯 Visão geral

Esta API é responsável por:

- Autenticação e autorização com JWT
- Gestão de usuários/perfis e regras de negócio
- Validação de payloads com Bean Validation
- Persistência com Spring Data JPA
- Exposição de documentação OpenAPI/Swagger

---

## 🧰 Stack tecnológica

- **Java 25**
- **Spring Boot 4.1.0**
  - web
  - data-jpa
  - security
  - validation
  - actuator
- **H2** (banco em memória para desenvolvimento/testes)
- **JJWT** (`io.jsonwebtoken`) para geração/validação de JWT
- **Argon2** (`argon2-jvm`) para hash de senhas
- **springdoc-openapi** para Swagger/OpenAPI
- **Lombok**
- **spring-dotenv** para carregar variáveis de `.env`
- **JUnit** para testes
- **Maven Wrapper** (`./mvnw`)

---

## 🏗 Arquitetura e organização

Pacote base: `com.finance_ai_backend.api`

```text
src/main/java/com/finance_ai_backend/api/
├── ApiApplication.java
├── beans/                       # configurações (Argon2, OpenAPI/Swagger etc.)
├── controller/                  # camada HTTP
├── domain/
│   ├── models/                  # entidades JPA
│   ├── dtos/                    # request/response
│   ├── repositories/            # Spring Data JPA
│   ├── enums/
│   ├── exceptions/
│   └── validations/             # validações customizadas
├── infra/                       # segurança, filtros e infraestrutura
├── mappers/                     # entidade <-> DTO
└── services/                    # regras de negócio
```

### Fluxo de requisição autenticada

1. `JwtAuthenticationFilter` intercepta a requisição e valida o token.
2. `Controller` recebe e valida o DTO.
3. `Service` aplica regras de negócio.
4. `Repository` persiste/consulta dados.
5. `Mapper` converte entidades e DTOs.
6. Exceções são tratadas por handlers globais de validação/autenticação.

---

## ✅ Pré-requisitos

- JDK 25 instalado
- Git instalado
- Maven global **não é obrigatório** (usar `./mvnw`)

---

## ⚙ Configuração de ambiente

O projeto usa `spring-dotenv`.  
Crie um arquivo `.env` na raiz do projeto com base no `.env.example`.

### Exemplo (`.env.example`)

```env
# Segurança JWT
API_SECURITY_TOKEN_SECRET=troque-por-um-segredo-forte

# Integração OCI (quando aplicável)
OCI_NAMESPACE=seu_namespace_aqui

# Perfil Spring
SPRING_PROFILES_ACTIVE=dev
```

> Nunca versione o arquivo `.env`.

---

## ▶ Como executar

```bash
./mvnw spring-boot:run
```

### Endpoints locais úteis

- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/docs.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- Health: `http://localhost:8080/actuator/health`
- H2 Console: `http://localhost:8080/h2-console`

### H2 (local)

- JDBC URL: `jdbc:h2:mem:financeai`
- Usuário: `sa`
- Senha: *(em branco)*

> Schema gerado automaticamente via Hibernate/JPA (`spring.jpa.hibernate.ddl-auto=create-drop`).

---

## 🧪 Testes

```bash
./mvnw test
```

---

## 📘 Documentação da API (Swagger)

Com a aplicação em execução:

- UI interativa: `http://localhost:8080/docs.html`
- Contrato OpenAPI: `http://localhost:8080/v3/api-docs`

---

## 👤 Usuário de testes

Para gerar usuário admin de teste em ambiente local:

```properties
admin-application=true
```

No arquivo `application.properties`.

---

## ⚠ Observações importantes

- Faça **pull da `main`** antes de iniciar qualquer alteração.
- Trabalhe sempre em branch própria (`feat/...`, `fix/...`, `chore/...`).
- Não commitar segredos (`.env`, tokens, senhas, chaves).
- Validar endpoints no **Postman** antes de finalizar a tarefa.
- Garantir build e testes passando localmente.

---

