"""Modelo 2: Perfil financeiro."""
from fastapi import APIRouter, Depends, HTTPException

from auth import verify_api_key
from controllers.features import montar_dataframe_ordenado, montar_features_financeiras
from dto.schemas import DadosFinanceirosInput, PerfilOutput
from infra.model_loader import registry

router = APIRouter(tags=["predicao"])


@router.post(
    "/predict/perfil",
    response_model=PerfilOutput,
    dependencies=[Depends(verify_api_key)],
)
def predict_perfil(data: DadosFinanceirosInput):
    try:
        artefatos = registry.get("perfil")
    except FileNotFoundError as exc:
        raise HTTPException(status_code=503, detail=str(exc)) from exc

    modelo = artefatos["modelo"]
    colunas = list(artefatos["colunas"])

    features = montar_features_financeiras(data)
    df = montar_dataframe_ordenado(features, colunas)

    try:
        perfil = modelo.predict(df)[0]
    except Exception as exc:
        raise HTTPException(status_code=400, detail=f"Erro ao gerar predicao: {exc}") from exc

    return PerfilOutput(perfil=str(perfil), features_calculadas=features)
