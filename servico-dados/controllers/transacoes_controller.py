"""Modelo 1: Classificacao de transacoes."""
from typing import List
from fastapi import APIRouter, Depends, HTTPException

from auth import verify_api_key
from dto.schemas import TransacaoInput, TransacaoOutput
from infra.model_loader import registry

router = APIRouter(tags=["predicao"])


@router.post(
    "/predict/transacoes",
    response_model=TransacaoOutput,
    dependencies=[Depends(verify_api_key)],
)
def predict_transacao(data: TransacaoInput):
    try:
        artefatos = registry.get("transacoes")
    except FileNotFoundError as exc:
        raise HTTPException(status_code=503, detail=str(exc)) from exc

    modelo = artefatos["modelo"]
    vetorizador = artefatos["vetorizador"]

    try:
        vetor = vetorizador.transform([data.descricao])
        categoria = modelo.predict(vetor)[0]
    except Exception as exc:
        raise HTTPException(status_code=400, detail=f"Erro ao gerar predicao: {exc}") from exc

    return TransacaoOutput(descricao=data.descricao, categoria=str(categoria))

@router.post(
    "/predict/lote_transacoes",
    response_model=List[TransacaoOutput],
    dependencies=[Depends(verify_api_key)],
)
def predict_transacao(data: List[TransacaoInput]):
    try:
        artefatos = registry.get("transacoes")
    except FileNotFoundError as exc:
        raise HTTPException(status_code=503, detail=str(exc)) from exc

    modelo = artefatos["modelo"]
    vetorizador = artefatos["vetorizador"]
    resultado = []

    for item in data:
        try:
            vetor = vetorizador.transform([item.descricao])
            categoria = modelo.predict(vetor)[0]
        except Exception as exc:
            raise HTTPException(status_code=400, detail=f"Erro ao gerar predicao: {exc}") from exc
        
        else:
            resultado.append(
                TransacaoOutput(descricao=item.descricao, categoria=str(categoria))
            )
    return resultado

