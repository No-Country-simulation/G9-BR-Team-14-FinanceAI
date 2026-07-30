import logging
from gensim.models import KeyedVectors

from infra import config
from infra.storage_connection import StorageConnection

logger = logging.getLogger(__name__) 

class RegistroModelos:
    def __init__(self) -> None:
        self.modelos: dict[str, dict[str, object | None]] = {}
        self.cliente = StorageConnection()

    async def load_all(self) -> None:
        self.modelos = {
            "transacoes": {
                "modelo": self.cliente.obtem_item_de_modelo(config.MODELO_TRANSACOES),
                "vetorizador": self.carregar_vetorizador() ,
            },
            "perfil": {
                "modelo": self.cliente.obtem_item_de_modelo(config.MODELO_PERFIL),
                "vetorizador": self.cliente.obtem_item_de_modelo(config.COLUNAS_PERFIL),
            },
            "sugestoes": {
                "modelo": self.cliente.obtem_item_de_modelo(config.MODELO_SUGESTOES),
                "vetorizador": self.cliente.obtem_item_de_modelo(config.COLUNAS_SUGESTOES),
            },
        }

    def carregar_vetorizador(self):
        logger.info("Carregando vetorizador de palavras")
        return KeyedVectors.load("modelos/cc.pt.300.kv", mmap='r')
    
    def get(self, nome: str) -> dict[str, object | None]:
        """Retorna os artefatos carregados de um modelo pelo nome."""
        if nome not in self.modelos:
            raise FileNotFoundError(
                f"Artefatos do modelo '{nome}' nao foram carregados. "
                f"Verifique se a API iniciou corretamente (lifespan)."
            )
        return self.modelos[nome]

    def status(self) -> dict[str, bool]:
        """Indica, para cada modelo, se seus artefatos foram carregados."""
        return {
            nome: all(valor is not None for valor in artefatos.values())
            for nome, artefatos in self.modelos.items()
        }


registro_modelos = RegistroModelos()