# API de Modelos Financeiros (FastAPI)

3 rotas, cada uma servindo um dos modelos treinados, protegidas por uma
chave de API simples e trocável a qualquer momento.

## 1. Estrutura

```
api-modelos/
├── .env                      # chave de API + caminhos de todos os artefatos
├── requirements.txt
└── app/
    ├── main.py                # rotas da API + montagem das features
    ├── auth.py                # verificação da chave de API
    ├── model_loader.py        # carregamento dos modelos/artefatos em memória
    ├── schemas.py              # formato de entrada/saída (JSON)
    └── models/                # coloque aqui os seus .joblib
        ├── modelo.joblib               # Modelo 1: classificação de transações
        ├── vetorizador.joblib          # Modelo 1: vetorizador de texto
        ├── modelo_perfil.joblib        # Modelo 2: perfil financeiro
        ├── colunas_perfil.joblib       # Modelo 2: ordem das 16 colunas
        ├── modelo_sugestoes.joblib     # Modelo 3: sugestões
        ├── colunas_sugestoes.joblib    # Modelo 3: ordem das 16 colunas
        └── nomes_sugestoes.joblib      # Modelo 3: nomes das 13 sugestões
```

## 2. Instalação

```bash
cd api-modelos
python -m venv venv
source venv/bin/activate      # Windows: venv\Scripts\activate
pip install -r requirements.txt
```

## 3. Colocar seus modelos

Copie os 7 arquivos `.joblib` para dentro de `app/models/`, com os nomes
acima (ou ajuste os caminhos correspondentes no `.env`).

## 4. Rodar a API

```bash
uvicorn app.main:app --reload --port 8000
```

Documentação interativa (Swagger): http://localhost:8000/docs

Verifique se tudo carregou certo:

```bash
curl http://localhost:8000/health
```

## 5. Trocar a chave de API

Edite `API_KEY` no `.env`. A API lê o arquivo do disco a cada requisição,
então a troca já vale na próxima chamada — sem reiniciar o servidor.

## 6. As 3 rotas

Todas exigem o header `X-API-Key`.

### `POST /predict/transacoes` — Modelo 1

Entrada: só a descrição da transação (texto). Ela passa pelo
`vetorizador.joblib` antes de ir para o modelo.

```bash
curl -X POST http://localhost:8000/predict/transacoes \
  -H "Content-Type: application/json" \
  -H "X-API-Key: troque-esta-chave-super-secreta" \
  -d '{"descricao": "supermerc"}'
```

```json
{ "descricao": "supermerc", "categoria": "Alimentação" }
```

### `POST /predict/perfil` — Modelo 2

Entrada: as 12 features "cruas" (os 4 calculados — `total_gasto`,
`percentual_gasto`, `percentual_investido`, `saldo` — são derivados
automaticamente no backend, não precisam ser enviados).

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

```json
{
  "perfil": "Saudavel",
  "features_calculadas": { "renda_mensal": 10000, "...": "..." }
}
```

### `POST /predict/sugestoes` — Modelo 3

Mesma entrada do `/predict/perfil` (reaproveita as 16 features). A saída
já vem traduzida: o vetor binário de 13 posições é cruzado com
`nomes_sugestoes.joblib` para você saber quais sugestões foram ativadas.

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

```json
{
  "sugestoes_ativas": ["manter_bom_controle_financeiro"],
  "vetor_bruto": [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1]
}
```

## 7. ⚠️ Ponto de atenção: fórmulas das features calculadas

Os campos `total_gasto`, `percentual_gasto`, `percentual_investido` e
`saldo` são calculados em `app/main.py`, na função
`montar_features_financeiras()`, assim:

```python
total_gasto = soma de todos os gasto_*
percentual_gasto = total_gasto / renda_mensal
percentual_investido = valor_investido / renda_mensal
saldo = renda_mensal - total_gasto - valor_investido
```

Essa é a leitura mais direta dos nomes das colunas, mas **confirme que bate
exatamente** com o código de pré-processamento que você usou no
treinamento original. Se a fórmula real for diferente (ex: `saldo` sem
descontar o investido, ou percentuais em escala 0–100 em vez de 0–1), o
modelo vai receber valores fora do padrão que ele aprendeu e a previsão
pode sair errada **sem nenhum erro de execução**. Se precisar, me passa a
fórmula exata usada no treino que eu ajusto essa função.

## 8. Ordem das colunas — por que não está hardcoded

Nenhuma das rotas assume a ordem das 16 colunas manualmente: `/predict/perfil`
e `/predict/sugestoes` sempre carregam `colunas_perfil.joblib` /
`colunas_sugestoes.joblib` do disco e reordenam o DataFrame com base neles
(`montar_dataframe_ordenado()` em `main.py`). Se o nome de alguma coluna
não bater com o que a função `montar_features_financeiras()` calcula, a API
retorna erro 500 explicando exatamente qual coluna está faltando — em vez
de silenciosamente prever errado.

## 9. Deploy em produção

Troque `--reload` por um processo gerenciado, ex:

```bash
uvicorn app.main:app --host 0.0.0.0 --port 8000 --workers 2
```

Rode atrás de um proxy (nginx) com HTTPS e considere empacotar com Docker.