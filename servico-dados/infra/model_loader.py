"""
Carregamento dos 3 modelos treinados e seus artefatos auxiliares
(vetorizador, listas de colunas, nomes de sugestoes).

Tudo e carregado UMA VEZ na inicializacao da API (lifespan) e mantido em
memoria. As colunas/ordem de features NUNCA sao fixadas no codigo (hardcode)
-- sempre vem dos arquivos colunas_*.joblib, exatamente como usado no
treinamento, para evitar previsao errada sem erro de execucao.
"""
import joblib

from infra import config
from infra.storage_connectio import StorageConnection


class RegistroModelos:
    def __init__(self) -> None:
        self.modelos: dict[str, dict[str, object | None]] = {}
        self.cliente = StorageConnection()

    async def load_local_all(self) -> None:
        """
            Essa função deve ser usada para quando o buckt estiver inacessivel por qualquer motivo
        """

        self.modelos = {
            "transacoes": {
                "modelo": joblib.load(f'modelos/{config.MODELO_TRANSACOES}'),
                "vetorizador": joblib.load(f'modelos/{config.VETORIZADOR_TRANSACOES}'),
            },
            "perfil": {
                "modelo": joblib.load(f'modelos/{config.MODELO_PERFIL}'),
                "colunas": joblib.load(f'modelos/{config.COLUNAS_PERFIL}'),
            },
            "sugestoes": {
                "modelo": joblib.load(f'modelos/{config.MODELO_SUGESTOES}'),
                "colunas": joblib.load(f'modelos/{config.COLUNAS_SUGESTOES}'),
                "nomes_sugestoes": joblib.load(f'modelos/{config.NOMES_SUGESTOES}'),
            },
        }

    async def load_all(self) -> None:
        self.modelos = {
            "transacoes": {
                "modelo": self.cliente.obtem_item_de_modelo(config.MODELO_TRANSACOES),
                "vetorizador": self.cliente.obtem_item_de_modelo(config.VETORIZADOR_TRANSACOES),
            },
            "perfil": {
                "modelo": self.cliente.obtem_item_de_modelo(config.MODELO_PERFIL),
                "colunas": self.cliente.obtem_item_de_modelo(config.COLUNAS_PERFIL),
            },
            "sugestoes": {
                "modelo": self.cliente.obtem_item_de_modelo(config.MODELO_SUGESTOES),
                "colunas": self.cliente.obtem_item_de_modelo(config.COLUNAS_SUGESTOES),
                "nomes_sugestoes": self.cliente.obtem_item_de_modelo(config.NOMES_SUGESTOES),
            },
        }

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


registry = RegistroModelos()