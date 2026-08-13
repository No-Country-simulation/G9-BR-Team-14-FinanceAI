# Ciência de Dados (treino dos modelos)

Notebooks e scripts usados para gerar dados sintéticos e treinar os 3
modelos de Machine Learning consumidos em produção pelo módulo
[`servico-dados`](../servico-dados/readme.md).

---

## Sumário

- [Visão geral](#visão-geral)
- [Estrutura do projeto](#estrutura-do-projeto)
- [Modelos treinados](#modelos-treinados)
- [Configuração de ambiente](#configuração-de-ambiente)
- [Como rodar os notebooks](#como-rodar-os-notebooks)
- [Geradores de dados sintéticos](#geradores-de-dados-sintéticos)
- [Observações importantes](#observações-importantes)

---

## Visão geral

Este módulo não faz parte da aplicação em execução — é o ambiente de
experimentação/treino usado para produzir os artefatos (`.joblib`/`.kv`)
que depois são publicados no bucket OCI Object Storage e carregados pelo
`servico-dados`. Os dados de treino são **sintéticos**, gerados por script
ou heurística, já que não há uma base real de usuários disponível para
treino.

---

## Estrutura do projeto

```
ciencia-dados/
├── requirements.txt                              # dependências do ambiente (arquivo simplificado, apenas libs de alto nível)
├── geradores/
│   ├── gera_dados_categoria.py                   # gera descrições de transações com ruído textual, por categoria
│   └── gera_dados_perfil_usuario.py              # gera perfis de gasto sintéticos (renda, investimento, gastos por categoria)
├── seeds/
│   ├── seed_categorias.json                      # seed de descrições/categorias usada no treino de classificação
│   └── seed_perfil_usuario_kmeans.py             # gera tabelas de percentual de gasto por categoria (soma ~100%) para os modelos K-means
├── modelos/                                       # artefatos gerados pelos notebooks (.joblib) + embeddings FastText (.kv/.vec)
├── treino_categoria_regressao_linear.ipynb       # Modelo 1: classificação de transações
├── treino_perfil_usuario_kmeans.ipynb            # Modelo 2: perfil financeiro
└── treino_sugestoes_kmeans_com_euristica.ipynb   # Modelo 3: sugestões de alertas
```

---

## Modelos treinados

### 1. `treino_categoria_regressao_linear.ipynb` — Classificação de transações

- **Dados**: descrições de transações geradas sinteticamente via LLM (1000
  itens por categoria), carregadas de `seeds/seed_categorias.json`.
- **Método**: vetorização de texto com embeddings FastText (pt-BR,
  `modelos/cc.pt.300.vec`/`.kv`, baixado de
  [fasttext.cc](https://fasttext.cc/docs/en/crawl-vectors.html)) seguida de
  `LogisticRegression` (regressão logística/linear) em um `Pipeline`
  scikit-learn.
- **Artefatos exportados**: `modelos/cc.pt.300.kv` (vetorizador/embeddings)
  e `modelos/classificador_despesas.joblib` (classificador). Em produção,
  o `servico-dados` carrega o vetorizador e o classificador separadamente
  — ver `services/FastTextVectorizer.py` e `MODELO_TRANSACOES` no
  `servico-dados`.

### 2. `treino_perfil_usuario_kmeans.ipynb` — Perfil financeiro

- **Dados**: tabela sintética gerada por `seeds/seed_perfil_usuario_kmeans.gerar_tabela()`
  (percentual de gasto por categoria + poupança, somando ~100%).
- **Método**: normalização (`scaler`) + agrupamento com `KMeans` sobre o
  total gasto e a poupança do usuário. A quantidade de clusters (5 perfis:
  *Comprometimento Excessivo*, *Capacidade Ociosa*, *Equilíbrio Precário*,
  *Gestão Consciente*, *Disciplina Financeira*) foi definida em uma rodada
  de treino anterior e é mantida fixa nas células seguintes.
- **Artefatos exportados**: `modelos/kmeans_perfil.joblib` (modelo) e
  `modelos/scaler_perfil.joblib` (normalizador). Correspondem a
  `MODELO_PERFIL`/`COLUNAS_PERFIL` no `servico-dados`.

### 3. `treino_sugestoes_kmeans_com_euristica.ipynb` — Sugestões de alertas

- **Dados**: mesmo gerador de perfil (`seed_perfil_usuario_kmeans.gerar_tabela()`),
  com um volume maior de linhas (100 mil).
- **Método**: `KMeans` sobre os percentuais normalizados por categoria,
  combinado com uma heurística que compara o gasto de cada categoria do
  usuário contra o centro do seu cluster para decidir quais alertas
  (`ALIMENTACAO`, `TRANSPORTE`, ..., `POUPANCA`) ativar — ver
  `services/sugestoes_perfil.py` no `servico-dados` para a lógica de
  decisão em produção.
- **Artefatos exportados**: `modelos/kmeans_perfil_sugestao.joblib` e
  `modelos/scaler_perfil_sugestao.joblib`. Correspondem a
  `MODELO_SUGESTOES`/`COLUNAS_SUGESTOES` no `servico-dados`.

---

## Configuração de ambiente

```bash
cd ciencia-dados
python -m venv .venv
source .venv/bin/activate      # Windows: .venv\Scripts\activate
pip install -r requirements.txt
```

Observação: os embeddings FastText (`modelos/cc.pt.300.vec`/`.kv`) são usados pelo notebook de classificação. As bibliotecas necessárias para rodar os notebooks (gensim, matplotlib e jupyterlab) estão listadas em `requirements.txt`.
---

## Como rodar os notebooks

```bash
jupyter lab
# ou
jupyter notebook
```

Abra o notebook correspondente ao modelo desejado e execute as células em
ordem. Ao final de cada notebook, os artefatos são salvos em `modelos/`.
Para publicá-los em produção, é preciso subir os arquivos gerados para o
bucket OCI Object Storage configurado no `servico-dados`, usando o mesmo
nome de objeto definido nas variáveis `MODELO_*`/`COLUNAS_*` de lá.

---

## Geradores de dados sintéticos

- **`geradores/gera_dados_categoria.py`** — a partir de um seed de
  descrições por categoria, gera variações com ruído textual (inserção,
  substituição e remoção de caracteres) para simular erros de digitação
  reais em descrições de transações.
- **`geradores/gera_dados_perfil_usuario.py`** — gera perfis financeiros
  sintéticos completos (renda, valor investido e gasto por categoria),
  usando pesos-base por categoria e faixas de variação (jitter) para
  simular diferentes padrões de consumo entre usuários.
- **`seeds/seed_perfil_usuario_kmeans.py`** — gera tabelas simplificadas de
  percentual de gasto por categoria (somando a um valor-alvo, tipicamente
  ~100%), usadas diretamente nos dois notebooks de K-means.

---

## Observações importantes

- Os dados usados em todo o módulo são **sintéticos**; não há dados reais
  de usuários neste repositório.
- As dependências estão listadas em `requirements.txt` (versão simplificada).
- Os embeddings FastText (`modelos/cc.pt.300.vec`/`.kv`) são arquivos
  grandes; confirme se devem mesmo ser versionados ou se cabem apenas
  localmente/no bucket, conforme a política de `.gitignore` do projeto.
- Ao gerar um novo artefato, atualize também as variáveis correspondentes
  no `.env` do `servico-dados` (`MODELO_*`/`COLUNAS_*`) e faça upload do
  arquivo para o bucket OCI configurado — o serviço não lê arquivos locais
  em produção.
