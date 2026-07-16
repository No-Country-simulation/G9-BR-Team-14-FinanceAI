"""
Carregamento dos 3 modelos treinados e seus artefatos auxiliares
(vetorizador, listas de colunas, nomes de sugestoes).

Tudo e carregado UMA VEZ na inicializacao da API (lifespan) e mantido em
memoria. As colunas/ordem de features NUNCA sao fixadas no codigo (hardcode)
-- sempre vem dos arquivos colunas_*.joblib, exatamente como usado no
treinamento, para evitar previsao errada sem erro de execucao.
"""
import os
import joblib

from infra import config
from infra.storage_connectio import StorageConnection


class RegistroModelos:
    def __init__(self) -> None:
        self.modelos: dict[str, dict[str, object | None]] = {}
        self.cliente = StorageConnection()

    async def load_all(self) -> None:
        self.modelos = {
            "transacoes": {
                "modelo": self.cliente.obtem_item(config.MODELO_TRANSACOES),
                "vetorizador": self.cliente.obtem_item(config.VETORIZADOR_TRANSACOES),
            },
            "perfil": {
                "modelo": self.cliente.obtem_item(config.MODELO_PERFIL),
                "colunas": self.cliente.obtem_item(config.COLUNAS_PERFIL),
            },
            "sugestoes": {
                "modelo": self.cliente.obtem_item(config.MODELO_SUGESTOES),
                "colunas": self.cliente.obtem_item(config.COLUNAS_SUGESTOES),
                "nomes_sugestoes": self.cliente.obtem_item(config.NOMES_SUGESTOES),
            },
        }


modelos_registrados = RegistroModelos()