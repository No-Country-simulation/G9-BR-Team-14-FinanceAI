# API de Modelos Financeiros (FastAPI)

API de inferência com 3 modelos de Machine Learning para apoio financeiro:

1. **Classificação de transações** por descrição textual  
2. **Classificação de perfil financeiro**  
3. **Geração de sugestões financeiras personalizadas**  

Todas as rotas de predição são protegidas por `X-API-Key`.

## Sumário

- [Visão Geral](#visão-geral)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Pré-requisitos](#pré-requisitos)
- [Instalação](#instalação)
- [Configuração dos Modelos](#configuração-dos-modelos)
- [Variáveis de Ambiente](#variáveis-de-ambiente)
- [Execução Local](#execução-local)
- [Autenticação](#autenticação)
- [Endpoints](#endpoints)
  - [`GET /health`](#get-health)
  - [`POST /predict/transacoes`](#post-predicttransacoes--modelo-1)
  - [`POST /predict/perfil`](#post-predictperfil--modelo-2)
  - [`POST /predict/sugestoes`](#post-predictsugestoes--modelo-3)
- [Observações Importantes](#observações-importantes)
- [Erros Comuns e Diagnóstico](#erros-comuns-e-diagnóstico)
- [Deploy em Produção](#deploy-em-produção)

---

## Visão Geral

Esta API carrega artefatos `.joblib` em memória e expõe rotas HTTP para predição.  
A chave de API é lida do `.env` e pode ser trocada sem reiniciar o servidor.

---

## Estrutura do Projeto

```text
api-modelos/
├── .env
├── requirements.txt
└── app/
    ├── main.py
    ├── auth.py
    ├── model_loader.py
    ├── schemas.py
    └── models/
        ├── modelo.joblib
        ├── vetorizador.joblib
        ├── modelo_perfil.joblib
        ├── colunas_perfil.joblib
        ├── modelo_sugestoes.joblib
        ├── colunas_sugestoes.joblib
        └── nomes_sugestoes.joblib
```

---

## Pré-requisitos

- Python 3.10+ (recomendado: 3.12)
- `pip`
- Ambiente virtual (`venv`)

---

## Instalação

```bash
cd api-modelos
python -m venv venv
```

### Ativar ambiente virtual

Linux/macOS:
```bash
source venv/bin/activate
```

Windows (PowerShell):
```powershell
venv\Scripts\Activate.ps1
```

Instalar dependências:
```bash
pip install -r requirements.txt
```

---

## Configuração dos Modelos

Copie os 7 arquivos `.joblib` para `app/models/` com os nomes esperados:

- `modelo.joblib`
- `vetorizador.joblib`
- `modelo_perfil.joblib`
- `colunas_perfil.joblib`
- `modelo_sugestoes.joblib`
- `colunas_sugestoes.joblib`
- `nomes_sugestoes.joblib`

Se os nomes/caminhos mudarem, ajuste no `.env`.

---

## Variáveis de Ambiente

Exemplo de `.env`:

```env
API_KEY=troque-esta-chave-super-secreta

MODELO_TRANSACOES_PATH=app/models/modelo.joblib
VETORIZADOR_PATH=app/models/vetorizador.joblib

MODELO_PERFIL_PATH=app/models/modelo_perfil.joblib
COLUNAS_PERFIL_PATH=app/models/colunas_perfil.joblib

MODELO_SUGESTOES_PATH=app/models/modelo_sugestoes.joblib
COLUNAS_SUGESTOES_PATH=app/models/colunas_sugestoes.joblib
NOMES_SUGESTOES_PATH=app/models/nomes_sugestoes.joblib
```

---

## Execução Local

```bash
uvicorn app.main:app --reload --port 8000
```

Acessos úteis:

- Swagger: `http://localhost:8000/docs`
- Healthcheck: `http://localhost:8000/health`

Teste rápido:

```bash
curl http://localhost:8000/health
```

---

## Autenticação

As rotas de predição exigem header:

```http
X-API-Key: sua-chave
```

A chave é lida do `.env` a cada requisição. Alterou no `.env`, já vale na próxima chamada.

---

## Endpoints

## `GET /health`

Verifica se a API está online.

Resposta esperada:
```json
{ "status": "ok" }
```

## `POST /predict/transacoes` — Modelo 1

Classifica categoria com base na descrição textual.

### Exemplo

```bash
curl -X POST http://localhost:8000/predict/transacoes \
  -H "Content-Type: application/json" \
  -H "X-API-Key: troque-esta-chave-super-secreta" \
  -d '{"descricao":"supermerc"}'
```

Resposta:
```json
{ "descricao": "supermerc", "categoria": "Alimentação" }
```

---

## `POST /predict/perfil` — Modelo 2

Entrada com 12 variáveis brutas.  
A API calcula automaticamente mais 4 features derivadas, totalizando 16.

### Exemplo

```bash
curl -X POST http://localhost:8000/predict/perfil \
  -H "Content-Type: application/json" \
  -H "X-API-Key: troque-esta-chave-super-secreta" \
  -d '{
    "renda_mensal": 10000,
    "valor_investido": 3000,
    "gasto_alimentacao": 500,
    "gasto_transporte": 300,
    "gasto_saude": 300,
    "gasto_moradia": 1000,
    "gasto_educacao": 200,
    "gasto_lazer": 200,
    "gasto_servicos": 100,
    "gasto_assinaturas": 50,
    "gasto_dividas": 100,
    "gasto_outras": 100
  }'
```

Resposta (exemplo):
```json
{
  "perfil": "Saudavel",
  "features_calculadas": {
    "renda_mensal": 10000
  }
}
```

---

## `POST /predict/sugestoes` — Modelo 3

Mesma entrada do endpoint de perfil.  
Saída traz sugestões ativas com base no vetor de predição multilabel.

### Exemplo

```bash
curl -X POST http://localhost:8000/predict/sugestoes \
  -H "Content-Type: application/json" \
  -H "X-API-Key: troque-esta-chave-super-secreta" \
  -d '{
    "renda_mensal": 10000,
    "valor_investido": 3000,
    "gasto_alimentacao": 500,
    "gasto_transporte": 300,
    "gasto_saude": 300,
    "gasto_moradia": 1000,
    "gasto_educacao": 200,
    "gasto_lazer": 200,
    "gasto_servicos": 100,
    "gasto_assinaturas": 50,
    "gasto_dividas": 100,
    "gasto_outras": 100
  }'
```

Resposta (exemplo):
```json
{
  "sugestoes_ativas": ["manter_bom_controle_financeiro"],
  "vetor_bruto": [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1]
}
```

---

## Observações Importantes

### 1) Consistência das fórmulas de features derivadas

As features abaixo são calculadas no backend:

- `total_gasto`
- `percentual_gasto`
- `percentual_investido`
- `saldo`

Lógica atual (em `montar_features_financeiras()`):

```python
total_gasto = soma de todos os gasto_*
percentual_gasto = total_gasto / renda_mensal
percentual_investido = valor_investido / renda_mensal
saldo = renda_mensal - total_gasto - valor_investido
```

Se essas fórmulas não forem **idênticas** às do treinamento, o modelo pode prever errado sem lançar erro técnico.

---

### 2) Ordem das colunas não é fixa no código

A API não “chuta” ordem de colunas.  
Ela usa:

- `colunas_perfil.joblib`
- `colunas_sugestoes.joblib`

Se faltar alguma coluna esperada, retorna erro explícito, evitando inferência silenciosamente incorreta.

---

### 3) Segurança básica

- Não versione `.env` no Git
- Troque `API_KEY` por valor forte em produção
- Considere autenticação robusta (JWT/OAuth2) para ambientes críticos

---

## Erros Comuns e Diagnóstico

- **401 Unauthorized**: `X-API-Key` ausente ou inválido
- **500 ao prever**: arquivo `.joblib` ausente/incompatível ou coluna faltando
- **Predições estranhas**: divergência entre pré-processamento de treino e API
- **Swagger abre, mas rota falha**: checar paths dos artefatos no `.env`

---

## Deploy em Produção

Não usar `--reload` em produção.

```bash
uvicorn app.main:app --host 0.0.0.0 --port 8000 --workers 2
```

Recomendações:

- Executar atrás de proxy reverso (Nginx)
- Habilitar HTTPS
- Containerizar com Docker
- Centralizar logs e monitoramento