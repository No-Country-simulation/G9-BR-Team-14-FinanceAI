"""Rota publica (sem autenticacao) para checar se os artefatos foram carregados."""
from fastapi import APIRouter

from infra.model_loader import registro_modelos

router = APIRouter(tags=["status"])


@router.get("/health")
def health_check():
    return {"status": "ok", "artefatos_carregados": registro_modelos.status()}
