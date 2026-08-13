# Finance AI API

API REST em Java/Spring Boot que fornece o backend do projeto **Finance AI**.

---

## Sumário

- [Visão geral](#visão-geral)
- [Stack tecnológica](#stack-tecnológica)
- [Arquitetura e organização do código](#arquitetura-e-organização-do-código)
- [Endpoints principais](#endpoints-principais)
- [Integração com o serviço de dados (ML)](#integração-com-o-serviço-de-dados-ml)
- [Pré-requisitos](#pré-requisitos)
- [Configuração de ambiente](#configuração-de-ambiente)
- [Perfis do Spring (`dev` / `prod`)](#perfis-do-spring-dev--prod)
- [Como rodar o projeto](#como-rodar-o-projeto)
- [Testes](#testes)
- [Usuário de teste](#usuário-de-teste)
- [Documentação da API (Swagger)](#documentação-da-api-swagger)
- [Observações importantes](#observações-importantes)

---

## Visão geral

Este serviço centraliza autenticação e regras de negócio do Finance AI, expondo endpoints REST para integração com frontend e demais módulos. Ele consome o módulo [`servico-dados`](../servico-dados/readme.md) (API FastAPI) para classificar transações, calcular o perfil financeiro do usuário e gerar sugestões de alertas com base em modelos de Machine Learning.

---

## Stack tecnológica

- **Java 25**
- **Spring Boot 4.1.0**
  - `spring-boot-starter-webmvc`
  - `spring-boot-starter-data-jpa`
  - `spring-boot-starter-security`
  - `spring-boot-starter-validation`
  - `spring-boot-starter-actuator`
  - `spring-boot-starter-restclient` (integração HTTP com `servico-dados`)
- **H2** (perfil `dev`, em arquivo) / **PostgreSQL** (perfil `prod`)
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

## Endpoints principais

- `POST /token` — autentica um usuário e gera o token JWT de acesso.
- `POST /token/blacklist` — invalida (logout) o token informado.
- `GET /usuario` — retorna os dados do usuário autenticado.
- `POST /api/v1/transacoes` — recebe um lote de transações do usuário autenticado, classifica cada uma via `servico-dados` e persiste no banco.
- `POST /api/v1/transacao` — classifica uma única transação sem persistir (consulta rápida).
- `POST /api/v1/analisar` — processa as transações salvas do usuário, calcula percentuais de gasto/poupança e chama `servico-dados` para gerar o perfil financeiro e as sugestões, persistindo o resultado.
- `GET /api/v1/perfil` — retorna o perfil financeiro e as sugestões já geradas para o usuário autenticado.

Detalhes completos de request/response de cada endpoint estão disponíveis no Swagger (ver seção [Documentação da API](#documentação-da-api-swagger)).

---

## Integração com o serviço de dados (ML)

O backend não treina nem executa modelos de ML diretamente: ele delega essas predições ao módulo [`servico-dados`](../servico-dados/readme.md) via HTTP, usando `RestClient` (`TransacoesService`). O fluxo típico:

1. `POST /api/v1/transacoes` (ou `/transacao`) envia as descrições para `POST /predict/lote_transacoes` (ou `/predict/transacoes`) em `servico-dados`, recebendo a categoria classificada para cada transação.
2. `POST /api/v1/analisar` soma os totais por categoria do usuário, calcula `porcentagem_gastos` e `porcentagem_poupanca`, e chama `POST /predict/perfil` e `POST /predict/sugestoes` em `servico-dados` para obter o perfil categorizado e as sugestões ativas.

A URL base e a chave de API do `servico-dados` são configuradas via variáveis de ambiente (ver próxima seção).

---

## Pré-requisitos

- JDK 25
- Git
- Não é necessário Maven instalado globalmente (usar `./mvnw`)

---

## Configuração de ambiente

1. Crie o arquivo `.env` na raiz do projeto (use `.env_exemple` como referência).
2. Defina as variáveis necessárias para execução local.
3. Garanta que `.env` esteja no `.gitignore`.

Variáveis lidas via `.env`/ambiente (`spring-dotenv`):

| Variável | Obrigatória em | Descrição |
| --- | --- | --- |
| `JWT_SECRET` | sempre | Segredo usado para assinar/validar os tokens JWT |
| `API_PREDICT_HOST` | sempre | URL base do módulo [`servico-dados`](../servico-dados/readme.md) (ex.: `http://localhost:8000`) |
| `API_KEY_PREDICT_HEADER_NAME` | sempre | Nome do header de API key esperado por `servico-dados` (deve casar com `API_KEY_HEADER_NAME` de lá) |
| `API_KEY_PREDICT` | sempre | Valor da chave de API enviada a `servico-dados` (deve casar com `API_KEY` de lá) |
| `AUTH_TOKEN_DURATION_HOURS` | opcional (default `1`) | Duração, em horas, do token JWT emitido |
| `SPRING_PROFILES_ACTIVE` | opcional (default `dev`) | Perfil ativo do Spring (`dev` ou `prod`) |
| `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD` | apenas perfil `prod` | Conexão com o PostgreSQL |
| `OCI_NAMESPACE`, `OCI_BUCKET_NAME`, `OCI_REGION`, `OCI_S3_ACCESS_KEY`, `OCI_S3_SECRET_KEY`, `OCI_PREFIX` | apenas perfil `prod` | Credenciais/config do bucket OCI Object Storage |

---

## Perfis do Spring (`dev` / `prod`)

O perfil ativo é controlado por `SPRING_PROFILES_ACTIVE` (padrão: `dev`).

- **`dev`** (`application-dev.properties`): usa **H2 em arquivo**
  (`jdbc:h2:file:./data/financeai`, persiste na pasta `data/`), com
  H2 Console habilitado em `/h2-console` e `spring.jpa.hibernate.ddl-auto=update`.
- **`prod`** (`application-prod.properties`): usa **PostgreSQL**
  (variáveis `DB_*`) e expõe configuração de OCI Object Storage
  (variáveis `OCI_*`).

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

Parâmetros padrão do H2 Console (perfil `dev`):

- JDBC URL: `jdbc:h2:file:./data/financeai`
- Usuário: `sa`
- Senha: (em branco)

Observação: o schema é atualizado automaticamente pelo Hibernate/JPA (`spring.jpa.hibernate.ddl-auto=update`) tanto em `dev` quanto em `prod`.

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
- `API_PREDICT_HOST`, `API_KEY_PREDICT_HEADER_NAME` e `API_KEY_PREDICT` devem estar sincronizados com a configuração real do `servico-dados` — divergências resultam em erro 401/timeout ao classificar transações ou gerar perfil/sugestões.
- Em PRs de documentação, informar explicitamente que não houve mudança de lógica da aplicação.