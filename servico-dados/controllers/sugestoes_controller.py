"""Modelo 3: Sugestoes."""
from fastapi import APIRouter, Depends, HTTPException

from auth import verify_api_key
from controllers.features import montar_dataframe_ordenado, montar_features_financeiras
from dto.schemas import DadosFinanceirosInput, SugestoesOutput
from infra.model_loader import registry

router = APIRouter(tags=["predicao"])


@router.post(
    "/predict/sugestoes",
    response_model=SugestoesOutput,
    dependencies=[Depends(verify_api_key)],
)
def predict_sugestoes(data: DadosFinanceirosInput):
    try:
        artefatos = registry.get("sugestoes")
    except FileNotFoundError as exc:
        raise HTTPException(status_code=503, detail=str(exc)) from exc

    modelo = artefatos["modelo"]
    colunas = list(artefatos["colunas"])
    nomes_sugestoes = list(artefatos["nomes_sugestoes"])

    features = montar_features_financeiras(data)
    df = montar_dataframe_ordenado(features, colunas)

    try:
        vetor_predito = modelo.predict(df)[0]
    except Exception as exc:
        raise HTTPException(status_code=400, detail=f"Erro ao gerar predicao: {exc}") from exc

    vetor_bruto = [int(v) for v in vetor_predito]

    if len(vetor_bruto) != len(nomes_sugestoes):
        raise HTTPException(
            status_code=500,
            detail=(
                f"O modelo retornou {len(vetor_bruto)} valores, mas "
                f"nomes_sugestoes.joblib tem {len(nomes_sugestoes)} nomes. "
                f"Verifique se os arquivos correspondem a mesma versao do modelo."
            ),
        )

    sugestoes_ativas = [
        nome for nome, ativo in zip(nomes_sugestoes, vetor_bruto) if ativo == 1
    ]

    return SugestoesOutput(sugestoes_ativas=sugestoes_ativas)
