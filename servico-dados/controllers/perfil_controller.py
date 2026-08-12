"""Modelo 2: Perfil financeiro."""
from fastapi import APIRouter, Depends, HTTPException

from auth import verify_api_key
from controllers.features import montar_dataframe_ordenado, montar_features_financeiras
from dto.schemas import DadosFinanceirosInput, PerfilOutput
from infra.model_loader import registro_modelos
from services.perfil_service import PerfilService

router = APIRouter(tags=["predicao"])


@router.post(
    "/predict/perfil",
    response_model=PerfilOutput,
    dependencies=[Depends(verify_api_key)],
)
def predict_perfil(data: DadosFinanceirosInput):
    perfil_data = PerfilService.prever_perfil(data.porcentagem_gastos, data.porcentagem_poupanca)
    print(perfil_data)
    return PerfilOutput(perfil=perfil_data['perfil'])