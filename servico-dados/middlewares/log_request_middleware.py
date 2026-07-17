import json
import logging

from starlette.middleware.base import BaseHTTPMiddleware
from starlette.requests import Request

logger = logging.getLogger("requests")


class LogRequestMiddleware(BaseHTTPMiddleware):
    """Middleware que imprime todas as entradas de dados recebidas pela API.

    Loga metodo, path, query params, headers e o corpo (body) de cada
    requisicao antes que ela seja processada pelos controllers.
    """

    async def dispatch(self, request: Request, call_next):
        body_bytes = await request.body()

        try:
            body_preview = json.loads(body_bytes) if body_bytes else None
        except (json.JSONDecodeError, UnicodeDecodeError):
            body_preview = body_bytes.decode("utf-8", errors="replace")

        log_data = {
            "method": request.method,
            "path": request.url.path,
            "query_params": dict(request.query_params),
            "headers": dict(request.headers),
            "body": body_preview,
        }

        print(f"[REQUEST] {json.dumps(log_data, ensure_ascii=False, indent=2)}")
        logger.info("Requisicao recebida: %s", log_data)

        # Recria o stream do corpo, ja que foi consumido acima,
        # para que os handlers seguintes ainda consigam le-lo.
        async def receive():
            return {"type": "http.request", "body": body_bytes, "more_body": False}

        request._receive = receive

        response = await call_next(request)
        return response
