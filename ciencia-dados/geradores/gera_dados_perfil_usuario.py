import random
import pandas as pd

random.seed()

CATEGORIAS = [
    "ALIMENTACAO", "TRANSPORTE", "SAUDE", "MORADIA", "EDUCACAO",
    "LAZER", "SERVICOS", "ASSINATURAS", "DIVIDAS", "OUTRAS",
]

RENDA_MIN = 1_500
RENDA_MAX = 20_000

# Peso-base médio de cada categoria no gasto total (soma = 1.0), refletindo
# padrões reais de consumo: Moradia costuma dominar o orçamento, Assinaturas
# e Serviços tendem a ser pequenos, Dívidas varia muito entre usuários.
PESOS_BASE = {
    "MORADIA": 0.30,
    "ALIMENTACAO": 0.18,
    "TRANSPORTE": 0.12,
    "DIVIDAS": 0.10,
    "SAUDE": 0.08,
    "EDUCACAO": 0.07,
    "LAZER": 0.07,
    "SERVICOS": 0.04,
    "OUTRAS": 0.02,
    "ASSINATURAS": 0.02,
}

# Faixa de variação aleatória (jitter) aplicada a cada peso-base, simulando
# que usuários diferentes gastam proporções diferentes em cada categoria.
# Dívidas tem faixa mais ampla (0 a 3x) para simular usuários sem nenhuma
# dívida até usuários fortemente endividados.
JITTER_POR_CATEGORIA = {
    "MORADIA": (0.6, 1.6),
    "ALIMENTACAO": (0.6, 1.6),
    "TRANSPORTE": (0.5, 1.8),
    "DIVIDAS": (0.0, 3.0),
    "SAUDE": (0.4, 2.2),
    "EDUCACAO": (0.3, 2.5),
    "LAZER": (0.4, 2.2),
    "SERVICOS": (0.4, 3.2),
    "OUTRAS": (0.4, 4.5),
    "ASSINATURAS": (0.5, 3.5),
}


def gerar_usuario_bruto() -> dict:
    renda_mensal = round(random.uniform(RENDA_MIN, RENDA_MAX), 2)

    percentual_investido_base = random.choice([
        random.uniform(0, 0.05),
        random.uniform(0.05, 0.15),
        random.uniform(0.15, 0.35),
    ])
    valor_investido = round(renda_mensal * percentual_investido_base, 2)

    percentual_gasto_total_base = random.choice([
        random.uniform(0.3, 0.6),
        random.uniform(0.6, 0.9),
        random.uniform(0.9, 1.3),
    ])
    gasto_total_alvo = renda_mensal * percentual_gasto_total_base

    # Aplica jitter em cima do peso-base de cada categoria (mantém a
    # proporção relativa "realista", mas com variação individual por usuário)
    pesos_ajustados = {}
    for categoria in CATEGORIAS:
        jitter_min, jitter_max = JITTER_POR_CATEGORIA[categoria]
        fator = random.uniform(jitter_min, jitter_max)
        pesos_ajustados[categoria] = PESOS_BASE[categoria] * fator

    soma_pesos = sum(pesos_ajustados.values())
    pesos_normalizados = {c: p / soma_pesos for c, p in pesos_ajustados.items()}

    linha = {
        "renda_mensal": renda_mensal,
        "valor_investido": valor_investido,
    }

    for categoria in CATEGORIAS:
        nome_categoria = f'gasto_{categoria.lower()}'
        linha[nome_categoria] = round(gasto_total_alvo * pesos_normalizados[categoria], 2)

    return linha


def run(n_usuarios: int = 3000) -> pd.DataFrame:
    usuarios = []
    for _ in range(n_usuarios):
        usuarios.append(gerar_usuario_bruto())

    return pd.DataFrame(usuarios)