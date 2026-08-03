# API de Modelos Financeiros (FastAPI)

API para inferência de **3 modelos de Machine Learning** no contexto financeiro, com autenticação via **X-API-Key** e carregamento de artefatos `.joblib`.

---

## 📌 Visão Geral

Esta API expõe três rotas de predição:

1. **Classificação de transações** (`/predict/transacoes`)
2. **Classificação de perfil financeiro** (`/predict/perfil`)
3. **Geração de sugestões financeiras** (`/predict/sugestoes`)

A autenticação é feita por chave de API, configurada em variável de ambiente, permitindo troca rápida sem alterar código-fonte.

---

## 🧱 Estrutura do Projeto

```text
api-modelos/
├── .env                              # chave de API + caminhos dos artefatos
├── requirements.txt
└── app/
    ├── main.py                       # rotas + montagem das features
    ├── auth.py                       # validação da X-API-Key
    ├── model_loader.py               # carregamento de modelos/artefatos
    ├── schemas.py                    # contratos de entrada/saída (Pydantic)
    └── models/
        ├── modelo.joblib             # Modelo 1: classificação de transações
        ├── vetorizador.joblib        # Modelo 1: vetorizador de texto
        ├── modelo_perfil.joblib      # Modelo 2: perfil financeiro
        ├── colunas_perfil.joblib     # Modelo 2: ordem das 16 colunas
        ├── modelo_sugestoes.joblib   # Modelo 3: sugestões
        ├── colunas_sugestoes.joblib  # Modelo 3: ordem das 16 colunas
        └── nomes_sugestoes.joblib    # Modelo 3: nomes das 13 sugestões
```

---

## ✅ Requisitos

- Python 3.10+ (recomendado 3.12)
- pip atualizado
- Git

---

## ⚙️ Instalação

```bash
cd api-modelos
python -m venv .venv

# Linux/macOS
source .venv/bin/activate

# Windows (PowerShell)
.venv\Scripts\Activate.ps1

pip install -r requirements.txt
```

---

## 🔐 Configuração de Ambiente

Crie o arquivo `.env` na raiz do projeto:

```env
API_KEY=troque-esta-chave-super-secreta

MODEL_TRANSACOES_PATH=app/models/modelo.joblib
VETORIZADOR_TRANSACOES_PATH=app/models/vetorizador.joblib

MODEL_PERFIL_PATH=app/models/modelo_perfil.joblib
COLUNAS_PERFIL_PATH=app/models/colunas_perfil.joblib

MODEL_SUGESTOES_PATH=app/models/modelo_sugestoes.joblib
COLUNAS_SUGESTOES_PATH=app/models/colunas_sugestoes.joblib
NOMES_SUGESTOES_PATH=app/models/nomes_sugestoes.joblib
```

> Nunca versione chave real de API em repositório público.

---

## 📦 Modelos e Artefatos

Copie os arquivos `.joblib` para `app/models/` com os nomes esperados  
**ou** ajuste os caminhos no `.env`.

---

## ▶️ Executando a API

### Desenvolvimento
```bash
uvicorn app.main:app --reload --port 8000
```

### Produção (exemplo)
```bash
uvicorn app.main:app --host 0.0.0.0 --port 8000 --workers 2
```

Documentação interativa (Swagger):
- `http://localhost:8000/docs`

Health check:
```bash
curl http://localhost:8000/health
```

---

## 🔑 Autenticação

Todas as rotas de predição exigem o header:

```http
X-API-Key: sua-chave
```

Se a chave estiver ausente ou inválida, a API retorna erro de autenticação.

---

## 🧪 Endpoints

## 1) `POST /predict/transacoes`

Classifica a categoria da transação a partir da descrição textual.

### Exemplo de request
```bash
curl -X POST http://localhost:8000/predict/transacoes \
  -H "Content-Type: application/json" \
  -H "X-API-Key: troque-esta-chave-super-secreta" \
  -d '{"descricao": "supermercado"}'
```

### Exemplo de response
```json
{
  "descricao": "supermercado",
  "categoria": "Alimentação"
}
```

---

## 2) `POST /predict/perfil`

Prediz perfil financeiro com base em 12 features de entrada.

### Exemplo de request
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

### Exemplo de response
```json
{
  "perfil": "Saudavel",
  "features_calculadas": {
    "renda_mensal": 10000
  }
}
```

---

## 3) `POST /predict/sugestoes`

Gera sugestões financeiras com base nas mesmas features do endpoint de perfil.

### Exemplo de request
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

### Exemplo de response
```json
{
  "sugestoes_ativas": ["manter_bom_controle_financeiro"],
  "vetor_bruto": [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1]
}
```

---

## ⚠️ Observações importantes

### 1) Fórmulas das features calculadas
As features derivadas são calculadas no backend (ex.: `total_gasto`, `percentual_gasto`, `percentual_investido`, `saldo`).

Valide se as fórmulas atuais estão **idênticas** às usadas no treinamento dos modelos.  
Diferenças de escala (0–1 vs 0–100) ou definição de saldo podem degradar as previsões sem erro explícito.

---

### 2) Ordem das colunas
A API não depende de ordem hardcoded para as 16 colunas.  
A ordem é carregada dos artefatos (`colunas_perfil.joblib` e `colunas_sugestoes.joblib`) para garantir consistência com o treinamento.

---

### 3) Segurança
- Não commitar `.env`
- Não expor `API_KEY` em screenshots, logs públicos ou código
- Rotacionar a chave periodicamente
- Em produção, usar HTTPS + proxy reverso (Nginx/Caddy)

---

## 🧰 Testes com Postman

Sugestão de coleção:
- Pasta `Auth`: variável `apiKey`
- Pasta `Predictions`: requests para `/predict/transacoes`, `/predict/perfil`, `/predict/sugestoes`
- Header padrão em todas:
  - `Content-Type: application/json`
  - `X-API-Key: {{apiKey}}`

---

## 🐞 Troubleshooting rápido

- **401/403**: chave ausente/incorreta no `X-API-Key`
- **500 ao prever**: arquivo `.joblib` ausente/incompatível ou coluna faltante
- **Erro de import**: dependências não instaladas corretamente (`pip install -r requirements.txt`)
- **Swagger não abre**: servidor não iniciou ou porta em uso

---

## 🚀 Boas práticas de PR

- Branch de feature dedicada (ex.: `feature/atualiza-readme-servico-dados`)
- Commits claros e pequenos
- PR com contexto, impacto e checklist de teste
- Solicitar revisão do mantenedor responsável

---

## 📄 Licença

Defina aqui a licença do projeto (ex.: MIT) conforme decisão do time.