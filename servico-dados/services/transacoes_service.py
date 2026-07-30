from sklearn.pipeline import Pipeline

from services.FastTextVectorizer import FastTextVectorizer
from infra.model_loader import registro_modelos

class TransacaoService:
    _pipeline = None

    @classmethod
    def _get_pipeline(cls) -> Pipeline:
        if cls._pipeline is None:
            modelo_info = registro_modelos.get("transacoes")
            print(modelo_info)
            cls._pipeline = Pipeline([
                ("fasttext", FastTextVectorizer(model=modelo_info["vetorizador"])),
                ("clf", modelo_info["modelo"]),
            ])
        return cls._pipeline

    @staticmethod
    def classificar_despesa(descricao: str, limite_confianca: float = 0.70):
        pipeline = TransacaoService._get_pipeline()
        categoria = pipeline.predict([descricao])[0]
        confianca = pipeline.predict_proba([descricao])[0].max()

        if confianca < limite_confianca:
            return "OUTROS", confianca

        return categoria, confianca