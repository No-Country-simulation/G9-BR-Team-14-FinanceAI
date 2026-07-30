"""Modelo 3: Sugestoes."""
from fastapi import APIRouter, Depends

from auth import verify_api_key
from dto.schemas import SugestoesInput, SugestoesOutput
from infra.model_loader import registro_modelos
from services.sugestoes_perfil import SugestoesPerfil

router = APIRouter(tags=["predicao"])


@router.post(
    "/predict/sugestoes",
    response_model=SugestoesOutput,
    dependencies=[Depends(verify_api_key)],
)
def predict_sugestoes(data: SugestoesInput):
    return SugestoesOutput(
        sugestoes_ativas=SugestoesPerfil.sugerir_alertas(data)
    )
