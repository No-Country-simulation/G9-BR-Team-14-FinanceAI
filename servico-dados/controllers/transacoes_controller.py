"""Modelo 1: Classificacao de transacoes."""
from typing import List
from fastapi import APIRouter, Depends, HTTPException

from auth import verify_api_key
from dto.schemas import TransacaoInput, TransacaoOutput
from infra.model_loader import registro_modelos
from services.transacoes_service import TransacaoService

router = APIRouter(tags=["predicao"])


@router.post(
    "/predict/transacoes",
    response_model=TransacaoOutput,
    dependencies=[Depends(verify_api_key)],
)
def predict_transacao(data: TransacaoInput):
    categoria, porcentagem_certeza = TransacaoService.classificar_despesa(data.descricao)
    return TransacaoOutput(descricao=data.descricao, categoria=str(categoria), porcentagem_certeza=porcentagem_certeza)

@router.post(
    "/predict/lote_transacoes",
    response_model=List[TransacaoOutput],
    dependencies=[Depends(verify_api_key)],
)
def predict_transacao(data: List[TransacaoInput]):
    resultado = []

    for item in data:
        categoria, porcentagem_certeza = TransacaoService.classificar_despesa(item.descricao)
        resultado.append(
            TransacaoOutput(descricao=item.descricao, categoria=str(categoria), porcentagem_certeza=porcentagem_certeza)
        )
    return resultado
