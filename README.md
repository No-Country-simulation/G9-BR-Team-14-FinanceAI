# Finance AI

Plataforma para gestão financeira pessoal com apoio de Machine Learning:
classificação automática de transações, geração de perfil financeiro e
sugestões de alertas personalizados.

O projeto é um monorepo dividido em 3 módulos independentes:

| Módulo | Papel | Stack | Documentação |
| --- | --- | --- | --- |
| [`backend/`](backend/README.md) | API principal: autenticação, cadastro de usuários e transações, orquestração da análise financeira | Java 25 + Spring Boot | [backend/README.md](backend/README.md) |
| [`servico-dados/`](servico-dados/readme.md) | API que expõe os modelos de ML treinados (classificação, perfil, sugestões) | Python + FastAPI | [servico-dados/readme.md](servico-dados/readme.md) |
| [`ciencia-dados/`](ciencia-dados/README.md) | Notebooks e scripts para gerar dados sintéticos e treinar os modelos consumidos pelo `servico-dados` | Python + Jupyter + scikit-learn | [ciencia-dados/README.md](ciencia-dados/README.md) |

---

## Arquitetura (visão geral)

```
                 ┌───────────────────────┐
   usuário  ───► │       backend         │
                 │  (Java / Spring Boot) │
                 │  auth, transações,    │
                 │  perfil, sugestões    │
                 └───────────┬───────────┘
                             │ HTTP (RestClient + API key)
                             ▼
                 ┌───────────────────────┐
                 │     servico-dados      │
                 │   (Python / FastAPI)   │
                 │ /predict/transacoes    │
                 │ /predict/perfil        │
                 │ /predict/sugestoes     │
                 └───────────┬───────────┘
                             │ carrega artefatos treinados
                             ▼
                 ┌───────────────────────┐
                 │   OCI Object Storage   │
                 │  (.joblib / .kv)       │
                 └───────────▲───────────┘
                             │ publica artefatos
                             │
                 ┌───────────┴───────────┐
                 │     ciencia-dados       │
                 │ notebooks de treino +   │
                 │ geradores de dados      │
                 │ sintéticos              │
                 └───────────────────────┘
```

- O **`backend`** autentica o usuário (JWT), recebe as transações e
  delega toda predição de ML ao **`servico-dados`** via HTTP, usando uma
  chave de API compartilhada entre os dois módulos.
- O **`servico-dados`** não tem lógica de negócio nem persistência
  própria: carrega os artefatos de modelo do bucket OCI Object Storage e
  só expõe rotas de predição.
- O **`ciencia-dados`** é o ambiente offline onde os modelos são
  treinados a partir de dados sintéticos; os artefatos resultantes são
  publicados no mesmo bucket consumido pelo `servico-dados`.

---

## Pré-requisitos gerais

- **Git**
- **JDK 25** (para o `backend`)
- **Python 3.12+** (para `servico-dados` e `ciencia-dados`)
- Acesso a um bucket **OCI Object Storage** com os modelos treinados
  (necessário para o `servico-dados` e para publicar novos modelos vindos
  de `ciencia-dados`)

---

## Como rodar o projeto localmente

Cada módulo tem seu próprio guia detalhado de configuração e execução.
Resumo rápido, na ordem recomendada de subida:

1. **`servico-dados`** (API de modelos) — configure o `.env` com as
   credenciais do bucket OCI e a chave de API, depois:
   ```bash
   cd servico-dados
   pip install -r requirements.txt
   uvicorn main:app --reload --port 8000
   ```
   Detalhes: [servico-dados/readme.md](servico-dados/readme.md)

2. **`backend`** (API principal) — configure o `.env` com `JWT_SECRET` e
   as variáveis `API_PREDICT_*` apontando para o `servico-dados` rodando
   no passo anterior, depois:
   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```
   Detalhes: [backend/README.md](backend/README.md)

3. **`ciencia-dados`** (opcional, só para retreinar modelos) —
   ```bash
   cd ciencia-dados
   pip install -r requiriments.txt
   jupyter lab
   ```
   Detalhes: [ciencia-dados/README.md](ciencia-dados/README.md)

---

## Observações importantes

- Nunca versionar arquivos `.env`, credenciais OCI ou artefatos de modelo
  (`.joblib`/`.kv`) — todos já estão cobertos pelo `.gitignore` na raiz.
- `backend` e `servico-dados` precisam compartilhar a mesma configuração
  de chave de API (`API_KEY_PREDICT`/`API_KEY_PREDICT_HEADER_NAME` no
  backend devem casar com `API_KEY`/`API_KEY_HEADER_NAME` no
  servico-dados).
- Para detalhes de endpoints, variáveis de ambiente e estrutura interna
  de cada módulo, consulte o README específico linkado na tabela acima.
