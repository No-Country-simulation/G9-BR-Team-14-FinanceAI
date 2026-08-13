# FinanceAI - Serviço de Inteligência Financeira

Microsserviço de Machine Learning do FinanceAI, desenvolvido em Python com FastAPI.

O serviço disponibiliza modelos preditivos utilizados pelo backend Java para:

- classificar transações financeiras;
- identificar o perfil financeiro do usuário;
- gerar sugestões e alertas personalizados.

Os artefatos dos modelos (`.joblib`) são carregados durante a inicialização da aplicação a partir do Oracle Cloud Infrastructure (OCI) Object Storage.

## Sumário

- [Visão geral](#visão-geral)
- [Responsabilidades](#responsabilidades)
- [Arquitetura](#arquitetura)
- [Modelos disponíveis](#modelos-disponíveis)
- [Estrutura do projeto](#estrutura-do-projeto)
- [API](#api)
- [Autenticação](#autenticação)
- [OCI Object Storage](#oci-object-storage)
- [Configuração](#configuração)
- [Instalação](#instalação)
- [Execução](#execução)
- [Documentação](#documentação)
- [Machine Learning](#machine-learning)
- [Integração com o Backend Java](#integração-com-o-backend-java)
- [Segurança](#segurança)

---

## Visão geral

O serviço de dados é responsável pela camada de Machine Learning do FinanceAI, separado do backend Java para que os modelos sejam executados como um serviço independente.

```
                   FinanceAI
                        │
                        ▼
               Backend Java
              Spring Boot API
                        │
                 REST + API Key
                        │
                        ▼
             Serviço de ML Python
                    FastAPI
                        │
          ┌─────────────┼─────────────┐
          ▼             ▼             ▼
    Transações       Perfil       Sugestões
       ML              ML             ML
                        │
                        ▼
               OCI Object Storage
                 Artefatos ML
```

## Responsabilidades

O serviço Python é responsável por:

- disponibilizar uma API HTTP para os modelos;
- carregar os modelos durante a inicialização;
- obter artefatos de ML no OCI Object Storage;
- classificar descrições de transações;
- identificar o perfil financeiro;
- gerar sugestões/alertas;
- validar as entradas utilizando Pydantic;
- proteger os endpoints de predição com API Key;
- disponibilizar documentação OpenAPI/Swagger;
- disponibilizar um endpoint de health check.

## Arquitetura

```
servico-dados/
├── main.py
├── auth.py
│
├── controllers/
│   ├── health_controller.py
│   ├── perfil_controller.py
│   ├── sugestoes_controller.py
│   └── transacoes_controller.py
│
├── dto/
│   └── schemas.py
│
├── infra/
│   ├── config.py
│   ├── model_loader.py
│   └── storage_connection.py
│
├── services/
│   ├── FastTextVectorizer.py
│   ├── perfil_service.py
│   ├── sugestoes_perfil.py
│   └── transacoes_service.py
│
└── middlewares/
    └── log_request_middleware.py
```

- **Controllers** — endpoints HTTP.
- **Services** — lógica de utilização dos modelos e geração dos resultados.
- **DTOs** — formatos de entrada/saída da API usando Pydantic.
- **Infraestrutura** — configuração, carregamento dos modelos e conexão com o armazenamento OCI.

## Modelos disponíveis

### 1. Classificação de transações

`POST /predict/transacoes`

Recebe a descrição textual de uma transação e retorna descrição, categoria e percentual de confiança da classificação.

O serviço utiliza um vetor semântico baseado em **FastText** para transformar a descrição textual em representação numérica antes da classificação (modelo `LogisticRegression`). Quando a confiança fica abaixo de 70%, a categoria retornada é `OUTRAS`.

Também existe classificação em lote:

`POST /predict/lote_transacoes` — recebe uma lista de descrições e retorna a classificação de cada item. É este endpoint que o backend Java utiliza.

### 2. Perfil financeiro

`POST /predict/perfil`

Entrada atual:

```json
{
  "porcentagem_gastos": 0.65,
  "porcentagem_poupanca": 0.20
}
```

O serviço utiliza um modelo de clustering (KMeans) para classificar o usuário em um dos perfis:

| Cluster | Perfil |
|---|---|
| 0 | Comprometimento Excessivo |
| 1 | Capacidade Ociosa |
| 2 | Equilíbrio Precário |
| 3 | Gestão Consciente |
| 4 | Disciplina Financeira |

> ℹ️ Este é o contrato implementado em `dto/schemas.py` (`DadosFinanceirosInput`). Desde 12/08/2026, o backend Java já envia o payload nesse formato, calculando as porcentagens antes de chamar este endpoint.

### 3. Sugestões e alertas

`POST /predict/sugestoes`

Entrada com valores relativos a 10 categorias: `ALIMENTACAO`, `TRANSPORTE`, `SAUDE`, `MORADIA`, `EDUCACAO`, `LAZER`, `SERVICOS`, `ASSINATURAS`, `DIVIDAS`, `POUPANCA`.

O modelo compara o perfil informado com os centros dos clusters (KMeans) e identifica categorias acima do limiar definido (padrão 15%), retornando uma lista de sugestões/alertas:

```json
{
  "sugestoes_ativas": [
    "[ALIMENTACAO] +18.4% → Você gasta muito com alimentação..."
  ]
}
```

### Artefatos dos modelos

Os artefatos utilizados pelo serviço não ficam versionados no código-fonte: são obtidos do OCI Object Storage durante a inicialização, via `infra/model_loader.py` e `infra/storage_connection.py`, carregados com `joblib`.

O vetor FastText utilizado na classificação textual é carregado localmente a partir de `modelos/cc.pt.300.kv`, obtido a partir dos vetores pré-treinados em português disponibilizados pelo FastText (https://fasttext.cc/docs/en/crawl-vectors.html), e mantido fora do OCI devido ao seu tamanho e ao impacto no tempo de inicialização do serviço

> **A confirmar:** o processo pelo qual `cc.pt.300.kv` chega ao ambiente de execução não está documentado nem automatizado no código — diferente dos demais artefatos, ele não vem do OCI Object Storage. Recomenda-se decidir e documentar a origem/forma de deploy desse arquivo (versionado via Git LFS, baixado por script de setup, etc.) antes da entrega.

## Estrutura do projeto

Ver [Arquitetura](#arquitetura) acima.

## API

Todos os endpoints de predição exigem o header configurado para a API Key, cujo nome é definido por `API_KEY_HEADER_NAME`.

> No ambiente atual (`.env`), esse header está configurado como `API-Key`. O nome pode mudar conforme o `.env` de cada ambiente — sempre confira o valor de `API_KEY_HEADER_NAME` antes de testar.

### Health Check

`GET /health` — não exige autenticação.

```bash
curl http://localhost:8000/health
```

```json
{
  "status": "ok",
  "artefatos_carregados": {
    "transacoes": true,
    "perfil": true,
    "sugestoes": true
  }
}
```

### Classificação de uma transação
`POST /predict/transacoes`
Header: `API-Key: <sua-chave>`

Entrada:
```json
{ "descricao": "supermercado" }
```

Saída:
```json
{
  "descricao": "supermercado",
  "categoria": "ALIMENTACAO",
  "porcentagem_certeza": 0.91
}
```

### Classificação em lote

`POST /predict/lote_transacoes`

Entrada:
```json
[
  { "descricao": "supermercado" },
  { "descricao": "uber" }
]
```

Saída:
```json
[
  { "descricao": "supermercado", "categoria": "ALIMENTACAO", "porcentagem_certeza": 0.91 },
  { "descricao": "uber", "categoria": "TRANSPORTE", "porcentagem_certeza": 0.87 }
]
```

Os valores acima são apenas exemplos de formato.

### Perfil financeiro

`POST /predict/perfil`

Entrada:
```json
{
  "porcentagem_gastos": 0.65,
  "porcentagem_poupanca": 0.20
}
```

Saída:
```json
{ "perfil": "Gestão Consciente" }
```

### Sugestões

`POST /predict/sugestoes`

Entrada:
```json
{
  "ALIMENTACAO": 120.0,
  "TRANSPORTE": 90.0,
  "SAUDE": 100.0,
  "MORADIA": 100.0,
  "EDUCACAO": 100.0,
  "LAZER": 100.0,
  "SERVICOS": 100.0,
  "ASSINATURAS": 100.0,
  "DIVIDAS": 100.0,
  "POUPANCA": 80.0
}
```

Saída:
```json
{
  "sugestoes_ativas": [
    "[ALIMENTACAO] +20.0% → Você gasta muito com alimentação. Tente reduzir refeições fora de casa."
  ]
}
```

## Autenticação

Os endpoints de predição são protegidos por API Key, configurada por `API_KEY`, com nome de header definido por `API_KEY_HEADER_NAME`.

```
API-Key: sua-chave
```

O backend Java utiliza essa mesma credencial para autenticar as chamadas entre os serviços.

## OCI Object Storage

O serviço Python utiliza o Oracle Cloud Infrastructure Object Storage para armazenar os artefatos dos modelos, via API compatível com S3 através da biblioteca `boto3`.

Endpoint construído como:
```
https://<namespace>.compat.objectstorage.<region>.oraclecloud.com
```

Fluxo de carregamento na inicialização:

```
Aplicação inicia
      │
      ▼
RegistroModelos.load_all()
      │
      ├── Modelo de transações
      ├── Modelo de perfil
      └── Modelo de sugestões
      │
      ▼
Artefatos carregados em memória
      │
      ▼
API disponível
```

Essa estratégia evita download dos modelos a cada requisição.

## Configuração

A aplicação utiliza `.env` via `python-dotenv`. Principais variáveis:

**API**
```
API_KEY_HEADER_NAME
API_KEY
```

**OCI**
```
OCI_NAMESPACE
OCI_REGION
OCI_BUCKET_NAME
OCI_S3_ACCESS_KEY
OCI_S3_SECRET_KEY
OCI_PREFIX
```

**Modelo de transações**
```
MODELO_TRANSACOES
```

**Modelo de perfil**
```
MODELO_PERFIL
COLUNAS_PERFIL
```

**Modelo de sugestões**
```
MODELO_SUGESTOES
COLUNAS_SUGESTOES
```

Nunca versionar o `.env` contendo credenciais reais.

## Instalação

Pré-requisitos: Python 3, acesso aos artefatos necessários, credenciais válidas para o OCI Object Storage.

Na pasta `servico-dados`:

```bash
python -m venv venv
source venv/bin/activate      # Windows: venv\Scripts\activate
pip install -r requiriments.txt
```

## Execução

```bash
uvicorn main:app --reload --port 8000
```

Para acesso externo:
```bash
uvicorn main:app --host 0.0.0.0 --port 8000
```

Em produção, recomenda-se um processo de execução gerenciado (ex.: múltiplos workers) e infraestrutura adequada ao ambiente de deploy.

## Documentação

Com o serviço em execução:

- Swagger UI: `http://localhost:8000/docs`
- ReDoc: `http://localhost:8000/redoc`
- OpenAPI: `http://localhost:8000/openapi.json`

## Machine Learning

Os notebooks e scripts utilizados na preparação e treinamento dos modelos estão em `ciencia-dados/`:

- `treino_categoria_regressao_linear.ipynb` (treina, na prática, uma **classificação** via `LogisticRegression` — o nome do arquivo é histórico e não reflete o algoritmo atual)
- `treino_perfil_usuario_kmeans.ipynb`
- `treino_sugestoes_kmeans_com_euristica.ipynb`

Também há scripts de geração de dados sintéticos e seeds.

Fluxo conceitual:

```
Dados
  │
  ▼
Preparação / Engenharia de dados
  │
  ▼
Treinamento dos modelos
  │
  ▼
Artefatos de ML
  │
  ▼
OCI Object Storage
  │
  ▼
Serviço Python
  │
  ▼
Predições
```

> **A confirmar:** as versões de `scikit-learn`/`numpy`/`pandas` usadas no ambiente de treino (`ciencia-dados/requiriments.txt`) diferem das usadas em produção (`servico-dados/requiriments.txt`). Recomenda-se validar que os modelos serializados carregam e preveem corretamente na versão de produção.

## Integração com o Backend Java

O serviço Python não é a API principal do FinanceAI — funciona como um serviço especializado de Machine Learning.

```
Backend Java
     │
     │ HTTP REST
     │ API Key
     ▼
FastAPI / Python
     │
     ▼
Modelos ML
     │
     ▼
Resultado
     │
     ▼
Backend Java
```

O backend utiliza os resultados para categorizar transações, gerar o perfil financeiro, gerar sugestões/alertas e armazenar o resultado associado ao usuário.

## Segurança

Não versionar: `.env`, API Keys, credenciais OCI, Access Keys, Secret Keys, outros segredos.

As credenciais devem ser fornecidas por variáveis de ambiente no ambiente de execução.
