from pydantic import BaseModel, Field

class TransacaoInput(BaseModel):
    descricao: str = Field(
        ...,
        examples=["supermerc"],
        description="Texto bruto da descricao da transacao (ex: 'supermercado', 'uber').",
    )


class TransacaoOutput(BaseModel):
    descricao: str
    categoria: str

class DadosFinanceirosInput(BaseModel):
    renda_mensal: float = Field(..., examples=[10000])
    valor_investido: float = Field(..., examples=[3000])
    gasto_alimentacao: float = Field(..., examples=[500])
    gasto_transporte: float = Field(..., examples=[300])
    gasto_saude: float = Field(..., examples=[300])
    gasto_moradia: float = Field(..., examples=[1000])
    gasto_educacao: float = Field(..., examples=[200])
    gasto_lazer: float = Field(..., examples=[200])
    gasto_servicos: float = Field(..., examples=[100])
    gasto_assinaturas: float = Field(..., examples=[50])
    gasto_dividas: float = Field(..., examples=[100])
    gasto_outras: float = Field(..., examples=[100])


class PerfilOutput(BaseModel):
    perfil: str
    features_calculadas: dict


class SugestoesOutput(BaseModel):
    sugestoes_ativas: list[str]