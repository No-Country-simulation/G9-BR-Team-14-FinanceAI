import pandas as pd
from dto.schemas import SugestoesInput

from infra.model_loader import registro_modelos

ALERTAS = {
    "ALIMENTACAO": "Você gasta muito com alimentação. Tente reduzir refeições fora de casa.",
    "TRANSPORTE": "Gastando muito com transporte. Tente meios alternativos (ônibus, bike, carona).",
    "SAUDE": "Gastos com saúde acima do perfil ideal. Verifique se todos são necessários.",
    "MORADIA": "Moradia está pesando no orçamento. Avalie se compensa a localização/custo.",
    "EDUCACAO": "Investimento em educação alto. Certifique-se de que está gerando retorno.",
    "LAZER": "Lazer acima do ideal do seu perfil. Reduza passeios/eventos por um tempo.",
    "SERVICOS": "Serviços diversos acima do padrão. Revise contratos e taxas.",
    "ASSINATURAS": "Muitas assinaturas. Cancele as que não usa.",
    "DIVIDAS": "Dívidas acima do ideal. Priorize quitar as com juros mais altos.",
    "POUPANCA": "Poupança abaixo do ideal. Tente guardar um pouco mais todo mês."
}


class SugestoesPerfil:
    @classmethod
    def alertas_usuario(cls,dados_usuario: pd.DataFrame, limiar: float = 15):
        """
        Retorna lista de alertas (multi-label) para o usuário.
        """
        colunas = dados_usuario.columns.tolist()        
        
        X_norm = registro_modelos.get("sugestoes")["vetorizador"].transform(dados_usuario)
        cluster = registro_modelos.get("sugestoes")["modelo"].predict(X_norm)[0]
        
        centro_norm = registro_modelos.get("sugestoes")["modelo"].cluster_centers_[cluster]
        
        centro_original = registro_modelos.get("sugestoes")["vetorizador"].inverse_transform(centro_norm.reshape(1, -1))[0]
        usuario_original = dados_usuario.values[0]
        
        alertas = []
        for i, col in enumerate(colunas):
            diff_pct = ((usuario_original[i] - centro_original[i]) / (centro_original[i] + 1e-6)) * 100
            
            if diff_pct > limiar and col in ALERTAS:
                alertas.append({
                    "categoria": col,
                    "diferenca_pct": round(diff_pct, 1),
                    "mensagem": ALERTAS[col]
                })
        
        return {
            "cluster": int(cluster),
            "alertas": alertas,
            "total_alertas": len(alertas)
        }

    @classmethod
    def sugerir_alertas(cls, dados_usuario: SugestoesInput):
        alertas_usuario = []
        novo = pd.DataFrame([{
            "ALIMENTACAO": dados_usuario.ALIMENTACAO,
            "TRANSPORTE": dados_usuario.TRANSPORTE,
            "SAUDE": dados_usuario.SAUDE,
            "MORADIA": dados_usuario.MORADIA,
            "EDUCACAO": dados_usuario.EDUCACAO,
            "LAZER": dados_usuario.LAZER,
            "SERVICOS": dados_usuario.SERVICOS,
            "ASSINATURAS": dados_usuario.ASSINATURAS,
            "DIVIDAS": dados_usuario.DIVIDAS,
            "POUPANCA": dados_usuario.POUPANCA
        }])

        resultado = cls.alertas_usuario(novo)
        
        for a in resultado['alertas']:
            alertas_usuario.append(f"[{a['categoria']}] +{a['diferenca_pct']}% → {a['mensagem']}")
        
        return alertas_usuario