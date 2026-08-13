# FinanceAI

Plataforma de inteligência financeira que utiliza Machine Learning para transformar transações financeiras em informações: classificação de gastos, perfil financeiro e sugestões personalizadas.

## Sobre o projeto

O FinanceAI é uma solução desenvolvida para apoiar a tomada de decisões financeiras a partir da análise de transações do usuário. A plataforma combina:

- **Backend Java/Spring Boot** para API, segurança, regras de negócio e persistência;
- **Python/FastAPI** para disponibilização dos modelos de Machine Learning;
- **Ciência de Dados** para treinamento e preparação dos modelos;
- **Banco de dados** para armazenamento das informações da aplicação;
- **Oracle Cloud Infrastructure (OCI)** para armazenamento dos artefatos de Machine Learning.

```
Transações financeiras
        │
        ▼
Classificação automática
        │
        ▼
Consolidação dos dados
        │
        ├───────────────┐
        ▼               ▼
Perfil financeiro    Sugestões
        │               │
        └───────┬───────┘
                ▼
        Inteligência financeira
```

## Problema

O acompanhamento das próprias finanças pode ser difícil quando o usuário possui grande quantidade de transações e não consegue identificar facilmente:

- onde está gastando;
- quais categorias comprometem mais sua renda;
- qual é seu comportamento financeiro;
- onde existem oportunidades de redução de gastos;
- quais ações podem melhorar sua organização financeira.

O FinanceAI utiliza dados e Machine Learning para automatizar parte dessa análise.

## Solução

**Backend Java** — aplicação principal: autenticação, autorização, gerenciamento de usuários, recebimento das transações, persistência, consolidação financeira, orquestração das análises, disponibilização dos resultados.

**Serviço Python** — camada de Machine Learning: classificação de transações, identificação de perfil financeiro, geração de sugestões e alertas.

**Ciência de Dados** — notebooks e scripts utilizados no desenvolvimento dos modelos.

**OCI** — os artefatos dos modelos são armazenados no Object Storage e carregados pelo serviço Python durante sua inicialização.

## Arquitetura

```
                         ┌───────────────────────┐
                         │       Cliente         │
                         │ Frontend / API Client │
                         └───────────┬───────────┘
                                     │
                                     ▼
                    ┌─────────────────────────────┐
                    │       BACKEND JAVA          │
                    │      Spring Boot 4.1        │
                    │                             │
                    │ • Autenticação              │
                    │ • Usuários                  │
                    │ • Transações                │
                    │ • Regras de negócio         │
                    │ • Persistência              │
                    └──────────────┬──────────────┘
                                   │
                         REST + API Key
                                   │
                                   ▼
                    ┌─────────────────────────────┐
                    │       SERVIÇO PYTHON        │
                    │          FastAPI            │
                    │                             │
                    │ • Classificação             │
                    │ • Perfil financeiro         │
                    │ • Sugestões/alertas         │
                    └──────────────┬──────────────┘
                                   │
                                   ▼
                    ┌─────────────────────────────┐
                    │          OCI                │
                    │     Object Storage          │
                    │                             │
                    │ • Modelos ML                │
                    │ • Artefatos de ML           │
                    └─────────────────────────────┘

                    ┌─────────────────────────────┐
                    │       Banco de Dados        │
                    │                             │
                    │ DEV  → H2                   │
                    │ PROD → PostgreSQL           │
                    └─────────────────────────────┘
```

## Fluxo principal

**1. Autenticação**

```
Usuário
  │
  ▼
POST /token
  │
  ▼
Token de acesso
```

As demais operações protegidas utilizam `Authorization: Bearer <token>`. *(O token é um identificador opaco (UUID) validado no backend — não é um JWT assinado.)*

**2. Envio das transações**

O cliente envia um lote de transações para `POST /api/v1/transacoes`. O backend recebe as transações, extrai as descrições, elimina duplicadas, envia ao serviço Python, recebe as categorias, associa às transações e persiste os dados.

**3. Classificação por Machine Learning**

O serviço Python recebe descrições como `"supermercado"`, `"uber"`, `"farmácia"`, `"aluguel"` e retorna as categorias correspondentes. A classificação textual utiliza representação baseada em FastText e um modelo de classificação (`LogisticRegression`) treinado. Quando a confiança fica abaixo do limite configurado (70%), a aplicação utiliza a categoria `OUTRAS`.

**4. Análise financeira**

Após o armazenamento das transações, o backend consolida os valores por categoria. A análise é acionada por `POST /api/v1/analisar`, que envia os dados consolidados ao serviço Python para gerar perfil financeiro e sugestões/alertas.

**5. Resultado**

O perfil e as sugestões são armazenados pelo backend e podem ser consultados por `GET /api/v1/perfil`.

## Capacidades de Machine Learning

| Capacidade | Tecnologia | Objetivo |
|---|---|---|
| Classificação de transações | Python + FastText + LogisticRegression | Categorizar despesas a partir da descrição |
| Perfil financeiro | Python + K-Means | Identificar o perfil financeiro |
| Sugestões/alertas | Python + K-Means + heurística | Identificar categorias que merecem atenção |

## Componentes do projeto

```
G9-BR-Team-14-FinanceAI/
│
├── backend/
│   └── API principal Java/Spring Boot
│
├── servico-dados/
│   └── Serviço Python/FastAPI de Machine Learning
│
├── ciencia-dados/
│   ├── notebooks de treinamento
│   ├── geradores de dados
│   └── seeds
│
├── frontend/
│   └── Estrutura reservada para o frontend (ainda não implementado)
│
└── data/
    └── Dados locais de desenvolvimento
```

## Tecnologias

**Backend**
Java 25 · Spring Boot 4.1 · Spring Web MVC · Spring Data JPA · Spring Security · Spring Validation · Spring RestClient · Token de acesso opaco · Argon2 · OpenAPI/Swagger · H2 · PostgreSQL

**Machine Learning**
Python · FastAPI · Scikit-Learn · Pandas · NumPy · Joblib · FastText · K-Means

**Cloud**
Oracle Cloud Infrastructure · OCI Object Storage · interface compatível com S3 via boto3

**Desenvolvimento**
Maven · Maven Wrapper · Jupyter Notebook · Git · GitHub

## Banco de dados

**Desenvolvimento:** H2, banco local em arquivo (`./data/financeai`).

**Produção:** PostgreSQL, conexão configurada por variáveis de ambiente.

## Segurança

**Cliente → Backend:** token de acesso opaco via Bearer.

**Backend → Serviço Python:** API Key.

As credenciais são fornecidas por variáveis de ambiente e não devem ser versionadas.

## APIs

**Backend Java**

| Método | Endpoint | Função |
|---|---|---|
| POST | `/token` | Geração de token de acesso |
| POST | `/token/blacklist` | Revogação de token |
| GET | `/usuario` | Usuário autenticado |
| POST | `/api/v1/transacoes` | Envio de transações |
| POST | `/api/v1/analisar` | Execução da análise |
| GET | `/api/v1/perfil` | Consulta do perfil |

**Serviço Python**

| Método | Endpoint | Função |
|---|---|---|
| GET | `/health` | Health check |
| POST | `/predict/transacoes` | Classificação de uma transação |
| POST | `/predict/lote_transacoes` | Classificação em lote |
| POST | `/predict/perfil` | Classificação do perfil |
| POST | `/predict/sugestoes` | Geração de sugestões/alertas |

## Documentação das APIs

**Backend** (com o backend em execução):
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI: `http://localhost:8080/v3/api-docs`

**Serviço Python** (com o serviço em execução):
- Swagger UI: `http://localhost:8000/docs`
- ReDoc: `http://localhost:8000/redoc`

## Execução local

**Backend**
```bash
cd backend
./mvnw spring-boot:run
```
Utiliza, por padrão, o profile `dev` e o banco H2 local.

**Serviço Python**
```bash
cd servico-dados
python -m venv venv
source venv/bin/activate
pip install -r requiriments.txt
uvicorn main:app --reload --port 8000
```
O serviço Python necessita das configurações de acesso aos artefatos de Machine Learning armazenados no OCI. 
Ver [nota sobre dependências ausentes](./servico-dados/readme.md#instalação).

## Ciência de Dados

O diretório `ciencia-dados/` contém os materiais utilizados no desenvolvimento dos modelos:

- `treino_categoria_regressao_linear.ipynb`
- `treino_perfil_usuario_kmeans.ipynb`
- `treino_sugestoes_kmeans_com_euristica.ipynb`

Também estão presentes scripts para geração de dados e seeds.

## Organização do desenvolvimento

```
                   FinanceAI
                        │
       ┌────────────────┼────────────────┐
       │                │                │
       ▼                ▼                ▼
   Backend Java     Serviço Python    Ciência de Dados
       │                │                │
       │                │                │
       └─────► ML ◄─────┘                │
                        │                │
                        ▼                │
                       OCI ◄─────────────┘
```

Essa separação permite que o backend concentre as regras de negócio, o Machine Learning seja desenvolvido e executado independentemente, os modelos sejam armazenados separadamente, e os artefatos possam ser atualizados sem incorporar os arquivos diretamente ao backend.

## Documentação dos componentes

- [Backend Java](./backend/README.md)
- [Serviço Python / Machine Learning](./servico-dados/readme.md)
- Ciência de Dados — notebooks em `ciencia-dados/`

## Links do projeto

- Repositório: [GitHub - G9-BR-Team-14-FinanceAI](https://github.com/No-Country-simulation/G9-BR-Team-14-FinanceAI)
- Organização/Simulação: [No Country](https://talent.nocountry.tech/dashboard)
- Gestão do projeto: [Trello - FinanceAI](https://trello.com/b/koyFysru/financeai-v2)
- Modelagem do banco: [MER - Autenticação e Autorização](https://dbdiagram.io/d/6a517a094ac62e474c7ded43)
- Guia do Hackathon: [Guia do Hackathon ONE G9](https://grupoalura.notion.site/Guia-do-Hackathon-ONE-G9-37d379bdd09b8059916af20865a502a6)

## Status do projeto

O repositório contém atualmente:

- backend Java/Spring Boot;
- autenticação por token de acesso opaco;
- persistência com H2/PostgreSQL;
- API REST documentada com OpenAPI;
- integração parcial com serviço Python (classificação de transações funcional; análise de perfil/sugestões com payload pendente de ajuste — ver [Fluxo principal](#fluxo-principal));
- serviço Python/FastAPI;
- modelos de classificação e clustering;
- integração com OCI Object Storage;
- notebooks e scripts de Ciência de Dados;
- testes automatizados no backend.

O frontend está atualmente representado no repositório apenas pela estrutura reservada para o componente (pasta vazia).

## Equipe

G9 BR Team 14 - FinanceAI

Projeto desenvolvido no contexto do Hackathon ONE / No Country G9.
