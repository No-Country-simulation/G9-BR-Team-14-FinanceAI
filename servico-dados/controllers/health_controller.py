"""Rota publica (sem autenticacao) para checar se os artefatos foram carregados."""
from fastapi import APIRouter

from infra.model_loader import registry

router = APIRouter(tags=["status"])


@router.get("/health")
def health_check():
    return {"status": "ok", "artefatos_carregados": registry.status()}
