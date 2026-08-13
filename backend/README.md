# FinanceAI - Backend Java

Backend principal do FinanceAI, desenvolvido em Java 25 com Spring Boot 4.1.

O backend concentra a API principal da aplicação, autenticação e autorização, persistência dos dados financeiros e orquestração das análises realizadas pelo serviço de Machine Learning em Python.

## Sumário

- [Visão geral](#visão-geral)
- [Responsabilidades](#responsabilidades)
- [Arquitetura](#arquitetura)
- [Stack tecnológica](#stack-tecnológica)
- [Estrutura do projeto](#estrutura-do-projeto)
- [Integração com o serviço Python](#integração-com-o-serviço-python)
- [Banco de dados](#banco-de-dados)
- [Autenticação e segurança](#autenticação-e-segurança)
- [Endpoints](#endpoints)
- [Documentação da API](#documentação-da-api)
- [Configuração](#configuração)
- [Execução](#execução)
- [Testes](#testes)
- [Perfis de execução](#perfis-de-execução)
- [Integração com OCI](#integração-com-oci)
- [Segurança](#segurança)

---

## Visão geral

O backend Java é a camada central da aplicação FinanceAI.

Ele recebe as requisições da aplicação cliente, autentica os usuários, armazena as transações financeiras e coordena a comunicação com o serviço Python responsável pelos modelos de Machine Learning.

Fluxo principal:

```
Cliente
   │
   ▼
Backend Java / Spring Boot
   │
   ├── Autenticação e autorização
   │
   ├── Persistência das transações
   │
   ├── Consulta e consolidação dos dados financeiros
   │
   └── Comunicação com o serviço Python
             │
             ├── Classificação de transações
             ├── Perfil financeiro
             └── Sugestões/alertas
```

## Responsabilidades

O backend é responsável principalmente por:

- cadastro (via usuário administrativo pré-configurado — ver [Usuário administrativo de desenvolvimento](#usuário-administrativo-de-desenvolvimento)) e autenticação de usuários;
- geração e revogação de tokens de acesso;
- proteção das rotas da aplicação;
- validação das requisições;
- recebimento de transações financeiras;
- persistência das transações;
- consolidação dos dados financeiros;
- comunicação com o serviço de Machine Learning;
- armazenamento do perfil financeiro calculado;
- disponibilização do perfil e das sugestões ao cliente;
- documentação da API utilizando OpenAPI/Swagger.

## Arquitetura

O projeto segue uma organização em camadas.

```
Controller
    │
    ▼
Service
    │
    ├──────────────► Serviço Python / ML
    │
    ▼
Repository
    │
    ▼
Banco de Dados
```

**Principais camadas**

- **Controllers** — exposição dos endpoints REST e recebimento das requisições.
- **Services** — regras de negócio e orquestração das operações.
- **Repositories** — persistência e consulta das entidades via Spring Data JPA.
- **Domain** — entidades, DTOs, enums, exceções e validações específicas do domínio.
- **Infra** — segurança e infraestrutura da aplicação.
- **Mappers** — conversões entre entidades e DTOs.

## Stack tecnológica

**Linguagem e framework**
- Java 25
- Spring Boot 4.1.0

**Spring**
- Spring Web MVC
- Spring Data JPA
- Spring Security
- Spring Validation
- Spring Actuator
- Spring RestClient

**Persistência**
- H2 para desenvolvimento
- PostgreSQL para produção

**Segurança**
- Token de acesso opaco (UUID), persistido e validado no banco de dados — **não é um JWT assinado**, apesar de algumas classes internas (`JwtAuthenticationEntryPoint`) manterem esse nome por herança de uma versão anterior do projeto
- Argon2 para hash de senhas
- Spring Security
- Blacklist de tokens revogados

**Documentação**
- Springdoc OpenAPI
- Swagger UI

**Outros**
- Lombok
- Maven Wrapper
- Spring Dotenv
- Bouncy Castle

## Estrutura do projeto

Pacote base: `com.finance_ai_backend.api`

```
src/
├── main/
│   ├── java/
│   │   └── com/finance_ai_backend/api/
│   │       ├── ApiApplication.java
│   │       ├── CriaUsuarioLineRunner.java
│   │       │
│   │       ├── beans/
│   │       │   ├── Argon2Password.java
│   │       │   └── OpenApiConfig.java
│   │       │
│   │       ├── controller/
│   │       │   ├── TokenController.java
│   │       │   ├── TransacoesController.java
│   │       │   └── UsuarioController.java
│   │       │
│   │       ├── domain/
│   │       │   ├── dtos/
│   │       │   ├── exceptions/
│   │       │   ├── models/
│   │       │   ├── repositories/
│   │       │   └── validations/
│   │       │
│   │       ├── infra/
│   │       │   ├── JwtAuthenticationEntryPoint.java
│   │       │   ├── SecurityConfig.java
│   │       │   ├── TokenAuthenticationFilter.java
│   │       │   └── ValidationExceptionHandler.java
│   │       │
│   │       ├── mappers/
│   │       └── services/
│   │
│   └── resources/
│       ├── application.properties
│       ├── application-dev.properties
│       └── application-prod.properties
│
└── test/
    └── java/
```

## Integração com o serviço Python

O backend utiliza o `RestClient` do Spring para consumir o serviço de Machine Learning.

A URL do serviço Python é configurada pela propriedade:

```
predict_api.host=${API_PREDICT_HOST}
```

A autenticação entre os serviços utiliza uma API Key configurável:

```
predict_api_key.header_name=${API_KEY_PREDICT_HEADER_NAME}
predict_api_key=${API_KEY_PREDICT}
```

**Classificação de transações**

Ao receber um lote de transações, o backend:

1. recebe as transações;
2. extrai as descrições;
3. elimina descrições duplicadas para reduzir chamadas;
4. envia as descrições ao serviço Python;
5. recebe a categoria atribuída a cada descrição;
6. associa a categoria às respectivas transações;
7. persiste as transações no banco.

Endpoint Python utilizado: `POST /predict/lote_transacoes`. Este contrato está **compatível** entre os dois lados hoje.

**Análise do perfil**

Após as transações serem persistidas, o backend consolida os valores por categoria e envia os dados ao serviço Python, chamando `POST /predict/perfil` e `POST /predict/sugestoes`. Os resultados são então persistidos no perfil do usuário.

## Banco de dados

O projeto possui dois perfis de persistência.

**Desenvolvimento**

O profile `dev` utiliza H2 em arquivo (não em memória):

```
jdbc:h2:file:./data/financeai
```

Configuração em `src/main/resources/application-dev.properties`. Os dados são armazenados no diretório `data/`.

**Produção**

O profile `prod` utiliza PostgreSQL:

```
jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}
```

Credenciais obtidas por variáveis de ambiente. Hibernate configurado com:

```
spring.jpa.hibernate.ddl-auto=update
```

## Autenticação e segurança

A API utiliza autenticação via **Bearer token opaco** (não um JWT assinado — ver nota em [Stack tecnológica](#stack-tecnológica)).

Fluxo:

```
Login
  │
  ▼
POST /token
  │
  ▼
Token de acesso (UUID)
  │
  ▼
Authorization: Bearer <token>
  │
  ▼
Rotas protegidas
```

O filtro `TokenAuthenticationFilter` intercepta as requisições e valida o token consultando o banco de dados (`TokenService.validarToken`). O projeto também implementa revogação de tokens por blacklist (`POST /token/blacklist`).

Senhas são protegidas utilizando Argon2. As sessões são configuradas como stateless (`SessionCreationPolicy.STATELESS`).

## Endpoints

**Autenticação**

| Método | Endpoint | Descrição | Autenticação |
|---|---|---|---|
| POST | `/token` | Gera token de acesso | Não |
| POST | `/token/blacklist` | Revoga token | Bearer |

**Usuário**

| Método | Endpoint | Descrição | Autenticação |
|---|---|---|---|
| GET | `/usuario` | Retorna informação do usuário autenticado | Bearer |

**Transações e análise**

| Método | Endpoint | Descrição | Autenticação |
|---|---|---|---|
| POST | `/api/v1/transacoes` | Recebe e persiste lote de transações | Bearer |
| POST | `/api/v1/analisar` | Executa análise financeira | Bearer |
| GET | `/api/v1/perfil` | Retorna perfil e sugestões do usuário | Bearer |

## Documentação da API

O projeto utiliza Springdoc OpenAPI. Com o backend em execução:

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI: `http://localhost:8080/v3/api-docs`

A aplicação também possui Actuator para monitoramento: `http://localhost:8080/actuator/health`.

Em desenvolvimento, a console H2 está disponível em `http://localhost:8080/h2-console`.

## Configuração

O profile ativo é definido por:

```
spring.profiles.active=${SPRING_PROFILES_ACTIVE:dev}
```

Na ausência de configuração específica, o projeto utiliza o profile `dev`.

O projeto utiliza `spring-dotenv` para leitura de variáveis de ambiente. Utilize o arquivo `.env_exemple` como referência. **Nunca versionar credenciais reais, tokens ou chaves privadas.**

## Execução

**Pré-requisitos**
- JDK 25
- Git
- Maven não precisa estar instalado globalmente (o projeto possui Maven Wrapper)

**Linux/macOS** (a partir da pasta `backend`):
```
./mvnw spring-boot:run
```

**Windows:**
```
mvnw.cmd spring-boot:run
```

Por padrão, a aplicação utiliza o profile `dev`.

**Build:**
```
./mvnw clean package
```

## Testes

```
./mvnw test
```

Os testes existentes incluem testes de aplicação (contexto), de repositórios (`@DataJpaTest`), de serviços e de tratamento de validações.

### Usuário administrativo de desenvolvimento

O projeto possui mecanismo para criação automática de um usuário administrativo quando `admin-application=true` (valor padrão em `application.properties`). As credenciais são definidas por `admin-application-username` e `admin-application-password`. **Em ambientes reais, não utilize credenciais padrão.**

> Observação: hoje este é o único mecanismo de criação de usuário — não há endpoint público de autocadastro (`POST /usuario` ou equivalente).

## Perfis de execução

**dev:** H2 em arquivo local, console H2, configurações voltadas ao desenvolvimento.

**prod:** PostgreSQL, variáveis de ambiente para conexão, configuração de integração com OCI Object Storage.

## Integração com OCI

O profile de produção possui propriedades para integração com OCI Object Storage:

```
oci.object-storage.namespace
oci.object-storage.bucket-name
oci.object-storage.region
oci.object-storage.access-key
oci.object-storage.secret-key
oci.object-storage.prefix
```

As credenciais devem ser fornecidas por variáveis de ambiente.

## Segurança

Não versionar: `.env`, senhas, tokens, access keys, secret keys, credenciais de banco, credenciais OCI.

Para desenvolvimento local, utilize valores próprios e não reutilize credenciais de produção.
