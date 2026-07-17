"""
Funcoes auxiliares compartilhadas pelos controllers de predicao (perfil e
sugestoes): montagem das features financeiras e do DataFrame ordenado
exatamente como o modelo foi treinado.
"""
import pandas as pd
from fastapi import HTTPException

from dto.schemas import DadosFinanceirosInput


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
def montar_features_financeiras(dados: DadosFinanceirosInput) -> dict:
    gastos = {
        "gasto_alimentação": dados.gasto_alimentacao,
        "gasto_transporte": dados.gasto_transporte,
        "gasto_saúde": dados.gasto_saude,
        "gasto_moradia": dados.gasto_moradia,
        "gasto_educação": dados.gasto_educacao,
        "gasto_lazer": dados.gasto_lazer,
        "gasto_serviços": dados.gasto_servicos,
        "gasto_assinaturas": dados.gasto_assinaturas,
        "gasto_dívidas": dados.gasto_dividas,
        "gasto_outras": dados.gasto_outras,
    }

    total_gasto = sum(gastos.values())
    percentual_gasto = (total_gasto / dados.renda_mensal) if dados.renda_mensal else 0.0
    percentual_investido = (
        (dados.valor_investido / dados.renda_mensal) if dados.renda_mensal else 0.0
    )
    saldo = dados.renda_mensal - total_gasto - dados.valor_investido

    return {
        "renda_mensal": dados.renda_mensal,
        "valor_investido": dados.valor_investido,
        **gastos,
        "total_gasto": total_gasto,
        "percentual_gasto": percentual_gasto,
        "percentual_investido": percentual_investido,
        "saldo": saldo,
    }


def montar_dataframe_ordenado(features: dict, colunas: list) -> pd.DataFrame:
    """Reordena/seleciona as features EXATAMENTE na ordem salva em colunas_*.joblib."""
    try:
        valores_ordenados = [features[coluna] for coluna in colunas]
    except KeyError as exc:
        raise HTTPException(
            status_code=500,
            detail=(
                f"O modelo espera a coluna {exc}, que nao foi encontrada nas features "
                f"calculadas. Confira se os nomes em colunas_*.joblib batem com os "
                f"nomes usados em montar_features_financeiras() (controllers/features.py)."
            ),
        ) from exc

    return pd.DataFrame([valores_ordenados], columns=list(colunas))
