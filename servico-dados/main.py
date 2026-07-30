from contextlib import asynccontextmanager

from fastapi import FastAPI

from controllers import (
    health_controller,
    perfil_controller,
    sugestoes_controller,
    transacoes_controller,
)
from infra.model_loader import registro_modelos
from middlewares.log_request_middleware import LogRequestMiddleware

import logging

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(name)s: %(message)s",
)

logger = logging.getLogger(__name__)

@asynccontextmanager
async def lifespan(app: FastAPI):
    logger.info("Carregando modelos")
    await registro_modelos.load_all()
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

# app.add_middleware(LogRequestMiddleware) # Ative em desenvolvimento

app.include_router(health_controller.router)
app.include_router(transacoes_controller.router)
app.include_router(perfil_controller.router)
app.include_router(sugestoes_controller.router)