# Finance AI API

API REST em Java/Spring Boot que fornece o backend do projeto **Finance AI**.

## Sumário
- [Stack tecnológica](#stack-tecnológica)
- [Arquitetura e organização do código](#arquitetura-e-organização-do-código)
- [Pré-requisitos](#pré-requisitos)
- [Configuração (variáveis de ambiente)](#configuração-variáveis-de-ambiente)
- [Como rodar o projeto](#como-rodar-o-projeto)
- [Testes](#testes)
- [Usuário de teste](#Usuário-de-testes)
- [Documentação da API (Swagger)](#documentação-da-api-swagger)

## Stack tecnológica
- **Java 25**
- **Spring Boot 4.1.0** (`web`, `data-jpa`, `security`, `validation`, `actuator`)
- **H2** como banco de dados em memória (schema gerado automaticamente pelo Hibernate/JPA)
- **JJWT** (`io.jsonwebtoken`) para geração/validação de tokens JWT
- **Argon2** (`argon2-jvm`) para hash de senhas
- **springdoc-openapi** para documentação Swagger/OpenAPI
- **Lombok** para reduzir boilerplate (getters/setters/builders)
- **spring-dotenv** para carregar variáveis de um arquivo `.env` na raiz automaticamente
- **JUnit** para testes de integração usando o próprio H2 em memória
- **Maven** (via wrapper `./mvnw`) como build tool

## Arquitetura e organização do código
O projeto segue uma organização em camadas dentro do pacote base `com.finance_ai_backend.api`:

```
src/main/java/com/finance_ai_backend/api/
├── ApiApplication.java          # classe main do Spring Boot
├── beans/                       # beans de configuração (Argon2PasswordEncoder, OpenAPI/Swagger)
├── controller/                  # controllers REST (camada de entrada HTTP)
├── domain/
│   ├── models/                  # entidades JPA (Usuario, Grupo, Permissao, tokens, etc.)
│   ├── dtos/                    # objetos de transferência de dados (request/response)
│   ├── repositories/            # interfaces Spring Data JPA
│   ├── enums/                   # enums de domínio (ex.: MotivoRevogacao)
│   ├── exceptions/              # exceções de negócio 
│   └── validations/             # validadores customizados (@EmailUnico, @UsernameUnico)
├── infra/                       # infraestrutura: Como filtros de acesso
├── mappers/                     # conversão entre entidades e DTOs
└── services/                    # regras de negócio
```

Fluxo típico de uma requisição autenticada:
1. `JwtAuthenticationFilter` intercepta a requisição, valida o token JWT (via `JwtService`) e popula o contexto de segurança.
2. O `Controller` recebe o DTO da requisição (validado com Bean Validation) e delega para o `Service`.
3. O `Service` aplica as regras de negócio e usa os `Repository` (Spring Data JPA) para persistência.
4. `Mapper`s convertem entre entidades JPA e DTOs para não vazar o modelo de domínio na API.
5. Erros de negócio/validação são tratados por `ValidationExceptionHandler` e pelo `JwtAuthenticationEntryPoint` (não autenticado).

## Pré-requisitos
- JDK 25
- Maven não é obrigatório: use o wrapper `./mvnw` incluso no repositório

## Configuração (variáveis de ambiente)
O projeto usa [`spring-dotenv`](https://github.com/paulschwarz/spring-dotenv), então basta criar um arquivo `.env` na raiz (veja `.env_exemple` como base).

## Como rodar o projeto
1. Rode a aplicação com o wrapper Maven:
   ```bash
   ./mvnw spring-boot:run
   ```
2. Acessos úteis:
   - API: http://localhost:8080
   - Swagger UI: http://localhost:8080/docs.html
   - Health check (Actuator): http://localhost:8080/actuator/health
   - Console web do H2: http://localhost:8080/h2-console
     - JDBC URL: `jdbc:h2:mem:financeai`
     - Usuário: `sa` / Senha: (em branco)

O schema do banco é criado/atualizado automaticamente pelo Hibernate/JPA
(`spring.jpa.hibernate.ddl-auto=create-drop`) a partir das entidades, sem
necessidade de migrações manuais.

## Testes
Os testes de integração usam o próprio H2 em memória, sem dependências externas:
```bash
./mvnw test
```

## Usuário de testes
Para gerar um usuário de teste voce pode configurar `admin-application=true` em `application.properties`.

## Documentação da API (Swagger)
Com a aplicação em execução, acesse `http://localhost:8080/docs.html` para a UI interativa do Swagger, ou `http://localhost:8080/v3/api-docs` para o contrato OpenAPI em JSON.
