from contextlib import asynccontextmanager

import pandas as pd
from fastapi import Depends, FastAPI, HTTPException

from infra.model_loader import RegistroModelos
from infra.storage_connectio import StorageConnection

from auth import verify_api_key
# from infra.model_loader import registry

# from .dto.schemas import (
#     DadosFinanceirosInput,
#     PerfilOutput,
#     SugestoesOutput,
#     TransacaoInput,
#     TransacaoOutput,
# )


@asynccontextmanager
async def lifespan(app: FastAPI):
    registro_modelos = RegistroModelos()
    registro_modelos.load_all
    yield

app = FastAPI(
    title="API de Modelos Financeiros",
    description=(
        "3 modelos: classificacao de transacoes, perfil financeiro e sugestoes, "
        "protegidos por chave de API (header X-API-Key)."
    ),
    version="1.0.0",
    lifespan=lifespan,
)


# ---------------------------------------------------------------------------
# Helper: monta as 16 features financeiras a partir do input do usuario.
#
# ATENCAO / TODO: as formulas de total_gasto, percentual_gasto,
# percentual_investido e saldo abaixo sao a interpretacao mais direta da
# descricao que voce passou. Confirme que elas batem EXATAMENTE com o
# codigo de pre-processamento usado no treinamento original -- se a formula
# de "saldo" ou de algum percentual for diferente la, o modelo vai receber
# valores diferentes dos que aprendeu e a previsao pode sair errada sem
# nenhum erro de execucao.
# ---------------------------------------------------------------------------
# def montar_features_financeiras(dados: DadosFinanceirosInput) -> dict:
#     gastos = {
#         "gasto_alimentação": dados.gasto_alimentacao,
#         "gasto_transporte": dados.gasto_transporte,
#         "gasto_saúde": dados.gasto_saude,
#         "gasto_moradia": dados.gasto_moradia,
#         "gasto_educação": dados.gasto_educacao,
#         "gasto_lazer": dados.gasto_lazer,
#         "gasto_serviços": dados.gasto_servicos,
#         "gasto_assinaturas": dados.gasto_assinaturas,
#         "gasto_dívidas": dados.gasto_dividas,
#         "gasto_outras": dados.gasto_outras,
#     }

#     total_gasto = sum(gastos.values())
#     percentual_gasto = (total_gasto / dados.renda_mensal) if dados.renda_mensal else 0.0
#     percentual_investido = (
#         (dados.valor_investido / dados.renda_mensal) if dados.renda_mensal else 0.0
#     )
#     saldo = dados.renda_mensal - total_gasto - dados.valor_investido

#     return {
#         "renda_mensal": dados.renda_mensal,
#         "valor_investido": dados.valor_investido,
#         **gastos,
#         "total_gasto": total_gasto,
#         "percentual_gasto": percentual_gasto,
#         "percentual_investido": percentual_investido,
#         "saldo": saldo,
#     }


# def montar_dataframe_ordenado(features: dict, colunas: list) -> pd.DataFrame:
#     """Reordena/seleciona as features EXATAMENTE na ordem salva em colunas_*.joblib."""
#     try:
#         valores_ordenados = [features[coluna] for coluna in colunas]
#     except KeyError as exc:
#         raise HTTPException(
#             status_code=500,
#             detail=(
#                 f"O modelo espera a coluna {exc}, que nao foi encontrada nas features "
#                 f"calculadas. Confira se os nomes em colunas_*.joblib batem com os "
#                 f"nomes usados em montar_features_financeiras() (app/main.py)."
#             ),
#         ) from exc

#     return pd.DataFrame([valores_ordenados], columns=list(colunas))


# @app.get("/health", tags=["status"])
# def health_check():
#     """Rota publica (sem autenticacao) para checar se os artefatos foram carregados."""
#     return {"status": "ok", "artefatos_carregados": registry.status()}


# # ---------------------------------------------------------------------------
# # Modelo 1: Classificacao de transacoes
# # ---------------------------------------------------------------------------
# @app.post(
#     "/predict/transacoes",
#     response_model=TransacaoOutput,
#     dependencies=[Depends(verify_api_key)],
#     tags=["predicao"],
# )
# def predict_transacao(data: TransacaoInput):
#     try:
#         artefatos = registry.get("transacoes")
#     except FileNotFoundError as exc:
#         raise HTTPException(status_code=503, detail=str(exc)) from exc

#     modelo = artefatos["modelo"]
#     vetorizador = artefatos["vetorizador"]

#     try:
#         vetor = vetorizador.transform([data.descricao])
#         categoria = modelo.predict(vetor)[0]
#     except Exception as exc:
#         raise HTTPException(status_code=400, detail=f"Erro ao gerar predicao: {exc}") from exc

#     return TransacaoOutput(descricao=data.descricao, categoria=str(categoria))


# # ---------------------------------------------------------------------------
# # Modelo 2: Perfil financeiro
# # ---------------------------------------------------------------------------
# @app.post(
#     "/predict/perfil",
#     response_model=PerfilOutput,
#     dependencies=[Depends(verify_api_key)],
#     tags=["predicao"],
# )
# def predict_perfil(data: DadosFinanceirosInput):
#     try:
#         artefatos = registry.get("perfil")
#     except FileNotFoundError as exc:
#         raise HTTPException(status_code=503, detail=str(exc)) from exc

#     modelo = artefatos["modelo"]
#     colunas = list(artefatos["colunas"])

#     features = montar_features_financeiras(data)
#     df = montar_dataframe_ordenado(features, colunas)

#     try:
#         perfil = modelo.predict(df)[0]
#     except Exception as exc:
#         raise HTTPException(status_code=400, detail=f"Erro ao gerar predicao: {exc}") from exc

#     return PerfilOutput(perfil=str(perfil), features_calculadas=features)


# # ---------------------------------------------------------------------------
# # Modelo 3: Sugestoes
# # ---------------------------------------------------------------------------
# @app.post(
#     "/predict/sugestoes",
#     response_model=SugestoesOutput,
#     dependencies=[Depends(verify_api_key)],
#     tags=["predicao"],
# )
# def predict_sugestoes(data: DadosFinanceirosInput):
#     try:
#         artefatos = registry.get("sugestoes")
#     except FileNotFoundError as exc:
#         raise HTTPException(status_code=503, detail=str(exc)) from exc

#     modelo = artefatos["modelo"]
#     colunas = list(artefatos["colunas"])
#     nomes_sugestoes = list(artefatos["nomes_sugestoes"])

#     features = montar_features_financeiras(data)
#     df = montar_dataframe_ordenado(features, colunas)

#     try:
#         vetor_predito = modelo.predict(df)[0]
#     except Exception as exc:
#         raise HTTPException(status_code=400, detail=f"Erro ao gerar predicao: {exc}") from exc

#     vetor_bruto = [int(v) for v in vetor_predito]

#     if len(vetor_bruto) != len(nomes_sugestoes):
#         raise HTTPException(
#             status_code=500,
#             detail=(
#                 f"O modelo retornou {len(vetor_bruto)} valores, mas "
#                 f"nomes_sugestoes.joblib tem {len(nomes_sugestoes)} nomes. "
#                 f"Verifique se os arquivos correspondem a mesma versao do modelo."
#             ),
#         )

#     sugestoes_ativas = [
#         nome for nome, ativo in zip(nomes_sugestoes, vetor_bruto) if ativo == 1
#     ]

#     return SugestoesOutput(sugestoes_ativas=sugestoes_ativas, vetor_bruto=vetor_bruto)