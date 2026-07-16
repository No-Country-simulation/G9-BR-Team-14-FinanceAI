from fastapi import HTTPException, Security, status
from fastapi.security import APIKeyHeader

from infra.config import API_KEY, API_KEY_HEADER_NAME


async def verify_api_key(
        request_api_key: str = Security(
            APIKeyHeader(name=API_KEY_HEADER_NAME, auto_error=False)
        )
    ) -> str:

    if not API_KEY:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="API_KEY nao configurada no servidor (verifique o .env).",
        )

    if not request_api_key or request_api_key != API_KEY:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Chave de API ausente ou invalida.",
        )

    return request_api_key