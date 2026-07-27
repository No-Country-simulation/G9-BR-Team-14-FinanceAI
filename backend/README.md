# Finance AI API

API REST em Java/Spring Boot que fornece o backend do projeto **Finance AI**.

---

## Sumário

- [Visão geral](#visão-geral)
- [Stack tecnológica](#stack-tecnológica)
- [Arquitetura e organização do código](#arquitetura-e-organização-do-código)
- [Pré-requisitos](#pré-requisitos)
- [Configuração de ambiente](#configuração-de-ambiente)
- [Como rodar o projeto](#como-rodar-o-projeto)
- [Testes](#testes)
- [Usuário de teste](#usuário-de-teste)
- [Documentação da API (Swagger)](#documentação-da-api-swagger)
- [Observações importantes](#observações-importantes)

---

## Visão geral

Este serviço centraliza autenticação e regras de negócio do Finance AI, expondo endpoints REST para integração com frontend e demais módulos.

---

## Stack tecnológica

- **Java 25**
- **Spring Boot 4.1.0**
  - `spring-boot-starter-web`
  - `spring-boot-starter-data-jpa`
  - `spring-boot-starter-security`
  - `spring-boot-starter-validation`
  - `spring-boot-starter-actuator`
- **H2** (banco em memória)
- **JJWT** (`io.jsonwebtoken`) para geração e validação de JWT
- **Argon2** (`argon2-jvm`) para hash de senhas
- **springdoc-openapi** para Swagger/OpenAPI
- **Lombok** para redução de boilerplate
- **spring-dotenv** para leitura de variáveis via `.env`
- **JUnit** para testes
- **Maven Wrapper** (`./mvnw`) para build e execução

---

## Arquitetura e organização do código

Pacote base: `com.finance_ai_backend.api`

Estrutura principal:

- `ApiApplication.java` — ponto de entrada da aplicação
- `beans/` — configurações e beans (ex.: encoder, OpenAPI)
- `controller/` — camada HTTP (endpoints REST)
- `domain/`
  - `models/` — entidades JPA
  - `dtos/` — objetos de request/response
  - `repositories/` — repositórios Spring Data JPA
  - `enums/` — enums de domínio
  - `exceptions/` — exceções de negócio
  - `validations/` — validações customizadas
- `infra/` — componentes de infraestrutura (ex.: filtros)
- `mappers/` — conversão entidade ↔ DTO
- `services/` — regras de negócio

Fluxo típico de requisição autenticada:

1. `JwtAuthenticationFilter` intercepta a requisição e valida o token (via `JwtService`).
2. O `Controller` recebe e valida o DTO.
3. O `Service` aplica regras de negócio.
4. O `Repository` persiste/consulta dados.
5. `Mapper` converte entidade para DTO de resposta.
6. Erros são tratados pelos handlers globais de exceção/autenticação.

---

## Pré-requisitos

- JDK 25
- Git
- Não é necessário Maven instalado globalmente (usar `./mvnw`)

---

## Configuração de ambiente

1. Crie o arquivo `.env` na raiz do projeto (use `.env.example` como referência).
2. Defina as variáveis necessárias para execução local.
3. Garanta que `.env` esteja no `.gitignore`.

---

## Como rodar o projeto

1. Entre na pasta do projeto.
2. Execute: `./mvnw spring-boot:run`
3. Acesse:
   - API: `http://localhost:8080`
   - Swagger UI: `http://localhost:8080/docs.html`
   - OpenAPI JSON: `http://localhost:8080/v3/api-docs`
   - Health (Actuator): `http://localhost:8080/actuator/health`
   - H2 Console: `http://localhost:8080/h2-console`

Parâmetros padrão do H2 Console:

- JDBC URL: `jdbc:h2:mem:financeai`
- Usuário: `sa`
- Senha: (em branco)

Observação: o schema é gerado automaticamente pelo Hibernate/JPA (`spring.jpa.hibernate.ddl-auto=create-drop`).

---

## Testes

Para rodar os testes:

- `./mvnw test`

Os testes usam H2 em memória e não dependem de banco externo.

---

## Usuário de teste

Para gerar usuário de teste, ajuste em `application.properties`:

- `admin-application=true`

---

## Documentação da API (Swagger)

Com a aplicação em execução:

- Swagger UI: `http://localhost:8080/docs.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

---

## Observações importantes

- Não versionar arquivos sensíveis (`.env`, chaves, segredos).
- Validar autenticação JWT antes de testar rotas protegidas.
- Manter o padrão de camadas (`controller` → `service` → `repository`) para facilitar manutenção.
- Em PRs de documentação, informar explicitamente que não houve mudança de lógica da aplicação.