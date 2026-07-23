import json
import logging

from starlette.middleware.base import BaseHTTPMiddleware
from starlette.requests import Request
from starlette.responses import Response

logger = logging.getLogger("requests")


class LogRequestMiddleware(BaseHTTPMiddleware):
    """Middleware que imprime todas as entradas e saídas de dados da API.

    Loga metodo, path, query params, headers e corpo (body) tanto da
    requisicao quanto da resposta.
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

        # Consome o corpo da resposta (stream) para poder logar
        response_body_chunks = [chunk async for chunk in response.body_iterator]
        response_body_bytes = b"".join(response_body_chunks)

        try:
            response_body_preview = (
                json.loads(response_body_bytes) if response_body_bytes else None
            )
        except (json.JSONDecodeError, UnicodeDecodeError):
            response_body_preview = response_body_bytes.decode("utf-8", errors="replace")

        response_log_data = {
            "status_code": response.status_code,
            "headers": dict(response.headers),
            "body": response_body_preview,
        }

        print(f"[RESPONSE] {json.dumps(response_log_data, ensure_ascii=False, indent=2)}")
        logger.info("Resposta enviada: %s", response_log_data)

        # Recria a resposta com o mesmo corpo, ja que o body_iterator
        # original foi consumido ao logar acima.
        return Response(
            content=response_body_bytes,
            status_code=response.status_code,
            headers=dict(response.headers),
            media_type=response.media_type,
        )