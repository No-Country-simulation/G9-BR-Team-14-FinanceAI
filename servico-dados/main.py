from contextlib import asynccontextmanager

from fastapi import FastAPI

from controllers import (
    health_controller,
    perfil_controller,
    sugestoes_controller,
    transacoes_controller,
)
from infra.model_loader import registry
from middlewares.log_request_middleware import LogRequestMiddleware


@asynccontextmanager
async def lifespan(app: FastAPI):
    await registry.load_local_all()
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

# app.add_middleware(LogRequestMiddleware)

app.include_router(health_controller.router)
app.include_router(transacoes_controller.router)
app.include_router(perfil_controller.router)
app.include_router(sugestoes_controller.router)