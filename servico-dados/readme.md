# Serviço de Dados (API de Modelos Financeiros)

API em **FastAPI** que expõe os 3 modelos de Machine Learning do **Finance
AI**: classificação de transações, perfil financeiro e sugestões de
alertas. É consumida internamente pelo `backend` (Java/Spring), que envia
os dados do usuário e recebe as predições.

---

## Sumário

- [Visão geral](#visão-geral)
- [Stack tecnológica](#stack-tecnológica)
- [Estrutura do projeto](#estrutura-do-projeto)
- [Carregamento dos modelos (OCI Object Storage)](#carregamento-dos-modelos-oci-object-storage)
- [Variáveis de ambiente](#variáveis-de-ambiente)
- [Como rodar o projeto](#como-rodar-o-projeto)
- [Autenticação](#autenticação)
- [Rotas](#rotas)
- [Observações importantes](#observações-importantes)

---

## Visão geral

Este serviço não persiste dados nem tem regras de negócio próprias: ele
apenas carrega os artefatos (`.joblib`/`.kv`) treinados em `ciencia-dados/`
e expõe rotas de predição. Toda a orquestração (autenticação de usuário,
persistência de transações, geração de perfil) fica no `backend`.

---

## Stack tecnológica

- **FastAPI** — framework web assíncrono
- **Uvicorn** — servidor ASGI
- **scikit-learn** / **joblib** — carregamento e execução dos modelos treinados
- **pandas** / **numpy** — montagem dos DataFrames de entrada dos modelos
- **pydantic** — validação de entrada/saída (`dto/schemas.py`)
- **boto3** — cliente S3-compatível usado para baixar os artefatos do
  **OCI Object Storage**
- **python-dotenv** — leitura de variáveis via `.env`

---

## Estrutura do projeto

```
servico-dados/
├── .env                        # variáveis de ambiente (não versionado)
├── requirements.txt
├── main.py                     # criação da app FastAPI, lifespan e rotas
├── auth.py                     # verificação da chave de API (header configurável)
├── controllers/
│   ├── health_controller.py    # GET /health (rota pública)
│   ├── transacoes_controller.py# Modelo 1: classificação de transações
│   ├── perfil_controller.py    # Modelo 2: perfil financeiro
│   ├── sugestoes_controller.py # Modelo 3: sugestões de alertas
│   └── features.py             # montagem/ordenação de DataFrames de features
├── dto/
│   └── schemas.py              # modelos pydantic de request/response
├── infra/
│   ├── config.py                # leitura das variáveis de ambiente
│   ├── model_loader.py          # registro/carregamento dos modelos em memória
│   └── storage_connection.py    # cliente S3 (boto3) apontando para OCI
├── middlewares/
│   └── log_request_middleware.py# middleware de log de requisições (opcional)
├── services/
│   ├── transacoes_service.py    # lógica de classificação de transações
│   ├── perfil_service.py        # lógica de predição de perfil
│   ├── sugestoes_perfil.py      # lógica de sugestões de alertas
│   └── FastTextVectorizer.py    # vetorização de texto (embeddings FastText)
└── modelos/
    └── cc.pt.300.kv             # embeddings FastText (pt-BR) usados na vetorização de texto
```

---

## Carregamento dos modelos (OCI Object Storage)

Diferente de uma configuração local com arquivos `.joblib` no disco, os
artefatos dos modelos são baixados em memória a partir de um bucket
**OCI Object Storage** (compatível com S3), via `infra/storage_connection.py`.

- No startup da aplicação (evento `lifespan` em `main.py`), `registro_modelos.load_all()`
  (`infra/model_loader.py`) baixa e carrega todos os artefatos configurados.
- Cada artefato é lido do bucket usando `OCI_PREFIX` como prefixo de chave
  e o nome do objeto vindo das variáveis `MODELO_*`/`COLUNAS_*`.
- O endpoint público `GET /health` permite checar se todos os artefatos
  foram carregados com sucesso, sem precisar de chave de API.

---

## Variáveis de ambiente

Definidas em `infra/config.py` (todas obrigatórias — a aplicação falha ao
subir se alguma estiver ausente):

| Variável | Descrição |
| --- | --- |
| `API_KEY_HEADER_NAME` | Nome do header HTTP usado para enviar a chave de API (ex.: `X-API-Key`) |
| `API_KEY` | Valor da chave de API exigida nas rotas protegidas |
| `OCI_NAMESPACE` | Namespace da conta OCI Object Storage |
| `OCI_REGION` | Região do bucket (ex.: `sa-saopaulo-1`) |
| `OCI_BUCKET_NAME` | Nome do bucket onde os modelos treinados estão armazenados |
| `OCI_S3_ACCESS_KEY` | Access key da credencial S3-compatível da OCI |
| `OCI_S3_SECRET_KEY` | Secret key da credencial S3-compatível da OCI |
| `OCI_PREFIX` | Prefixo (pasta) dentro do bucket onde os artefatos estão |
| `MODELO_TRANSACOES` | Nome do objeto do modelo de classificação de transações |
| `MODELO_PERFIL` | Nome do objeto do modelo de perfil financeiro |
| `COLUNAS_PERFIL` | Nome do objeto com a ordem das colunas usadas pelo modelo de perfil |
| `MODELO_SUGESTOES` | Nome do objeto do modelo de sugestões |
| `COLUNAS_SUGESTOES` | Nome do objeto com a ordem das colunas usadas pelo modelo de sugestões |

Crie um arquivo `.env` na raiz de `servico-dados/` com essas variáveis
preenchidas antes de rodar o serviço. Nunca versionar o `.env` real.

---

## Como rodar o projeto

```bash
cd servico-dados
python -m venv .venv
source .venv/bin/activate      # Windows: .venv\Scripts\activate
pip install -r requirements.txt
```

Preencha o `.env` (ver seção anterior) e rode:

```bash
uvicorn main:app --reload --port 8000
```

- Swagger UI: `http://localhost:8000/docs`
- OpenAPI JSON: `http://localhost:8000/openapi.json`
- Healthcheck: `http://localhost:8000/health`

---

## Autenticação

Todas as rotas de `/predict/*` são protegidas por uma chave de API
verificada em `auth.py` (`verify_api_key`). O nome do header **não é fixo**:
é definido pela variável `API_KEY_HEADER_NAME`. Exemplo, assumindo que
`API_KEY_HEADER_NAME=X-API-Key`:

```bash
curl -X POST http://localhost:8000/predict/transacoes \
  -H "Content-Type: application/json" \
  -H "X-API-Key: <valor de API_KEY>" \
  -d '{"descricao": "supermerc"}'
```

`GET /health` é a única rota pública (não exige chave de API).

---

## Rotas

### `GET /health`

Rota pública. Retorna o status dos artefatos carregados em memória:

```json
{ "status": "ok", "artefatos_carregados": { "...": "..." } }
```

### `POST /predict/transacoes` — Modelo 1 (classificação de transações)

Entrada: descrição textual da transação. O texto passa por vetorização
(FastText) antes de ir para o modelo.

```json
// request
{ "descricao": "supermerc" }

// response
{ "descricao": "supermerc", "categoria": "ALIMENTACAO", "porcentagem_certeza": 0.93 }
```

### `POST /predict/lote_transacoes` — Modelo 1 em lote

Mesma lógica de `/predict/transacoes`, mas recebe e devolve uma **lista**
de itens (usado pelo `backend` ao salvar várias transações de uma vez).

```json
// request
[{ "descricao": "supermerc" }, { "descricao": "uber" }]

// response
[
  { "descricao": "supermerc", "categoria": "ALIMENTACAO", "porcentagem_certeza": 0.93 },
  { "descricao": "uber", "categoria": "TRANSPORTE", "porcentagem_certeza": 0.88 }
]
```

### `POST /predict/perfil` — Modelo 2 (perfil financeiro)

Entrada: dois percentuais já calculados pelo backend (percentual gasto e
percentual investido/poupado em relação à renda).

```json
// request
{ "porcentagem_gastos": 0.62, "porcentagem_poupanca": 0.15 }

// response
{ "perfil": "Saudavel" }
```

### `POST /predict/sugestoes` — Modelo 3 (sugestões de alertas)

Entrada: valores gastos em cada categoria, mais o valor poupado/investido
(`POUPANCA`). A saída traz a lista de sugestões/alertas ativados para o
perfil informado.

```json
// request
{
  "ALIMENTACAO": 500,
  "TRANSPORTE": 300,
  "SAUDE": 300,
  "MORADIA": 1000,
  "EDUCACAO": 200,
  "LAZER": 200,
  "SERVICOS": 100,
  "ASSINATURAS": 50,
  "DIVIDAS": 100,
  "POUPANCA": 3000
}

// response
{ "sugestoes_ativas": ["manter_bom_controle_financeiro"] }
```

---

## Observações importantes

- O middleware de log de requisições (`LogRequestMiddleware`) existe em
  `middlewares/log_request_middleware.py`, mas está **comentado** em
  `main.py` — ative-o manualmente em ambiente de desenvolvimento se
  precisar depurar requisições.
- Os nomes de todos os artefatos (`MODELO_*`/`COLUNAS_*`) e o prefixo do
  bucket são configuráveis via `.env`; nenhuma rota assume nomes de arquivo
  fixos, então o mesmo código serve para trocar de modelo sem alterar
  código-fonte.
- Este serviço não deve ser exposto publicamente sem revisar CORS e a
  chave de API — hoje a única proteção das rotas de predição é a
  verificação de `API_KEY`.
- Não versionar `.env`, credenciais OCI ou os artefatos `.joblib`/`.kv`
  reais fora do bucket configurado.
