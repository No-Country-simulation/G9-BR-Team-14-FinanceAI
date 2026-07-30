from typing import List
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
    pocentagem_certeza: float

class DadosFinanceirosInput(BaseModel):
    procentagem_gastos: float
    porcentagem_poupanca: float


class PerfilOutput(BaseModel):
    perfil: str

class SugestoesInput(BaseModel):
    ALIMENTACAO: float = Field(100.0)
    TRANSPORTE: float = Field(100.0)
    SAUDE: float = Field(100.0)
    MORADIA: float = Field(100.0)
    EDUCACAO: float = Field(100.0)
    LAZER: float = Field(100.0)
    SERVICOS: float = Field(100.0)
    ASSINATURAS: float = Field(100.0)
    DIVIDAS: float = Field(100.0)
    POUPANCA: float = Field(100.0)
   
class SugestoesOutput(BaseModel):
    sugestoes_ativas: list[str]