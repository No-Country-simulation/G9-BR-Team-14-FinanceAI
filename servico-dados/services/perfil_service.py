import pandas as pd

from infra.model_loader import registro_modelos

class PerfilService:

    @staticmethod
    def prever_perfil(total_gastos: float, poupanca: float) -> dict:
        PERFIS = {
            0: "Comprometimento Excessivo",
            1: "Capacidade Ociosa",
            2: "Equilíbrio Precário",
            3: "Gestão Consciente",
            4: "Disciplina Financeira",
        }

        entrada = pd.DataFrame([[total_gastos, poupanca]], columns=["TOTAL_GASTOS", "POUPANCA"])
        entrada["TOTAL"] = entrada["TOTAL_GASTOS"] + entrada["POUPANCA"]
        
        entrada_norm = registro_modelos.get('perfil')['vetorizador'].transform(entrada)
        
        cluster = int(registro_modelos.get('perfil')['modelo'].predict(entrada_norm)[0])
        
        return {
            "cluster": cluster,
            "perfil": PERFIS.get(cluster, "Desconhecido"),
        }
