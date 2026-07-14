import random
import pandas as pd

random.seed()

CATEGORIAS = [
    "Alimentação", "Transporte", "Saúde", "Moradia", "Educação",
    "Lazer", "Serviços", "Assinaturas", "Dívidas", "Outras",
]

RENDA_MIN = 1500
RENDA_MAX = 20000

# Peso-base médio de cada categoria no gasto total (soma = 1.0), refletindo
# padrões reais de consumo: Moradia costuma dominar o orçamento, Assinaturas
# e Serviços tendem a ser pequenos, Dívidas varia muito entre usuários.
PESOS_BASE = {
    "Moradia": 0.30,
    "Alimentação": 0.18,
    "Transporte": 0.12,
    "Dívidas": 0.10,
    "Saúde": 0.08,
    "Educação": 0.07,
    "Lazer": 0.07,
    "Serviços": 0.04,
    "Outras": 0.02,
    "Assinaturas": 0.02,
}

# Faixa de variação aleatória (jitter) aplicada a cada peso-base, simulando
# que usuários diferentes gastam proporções diferentes em cada categoria.
# Dívidas tem faixa mais ampla (0 a 3x) para simular usuários sem nenhuma
# dívida até usuários fortemente endividados.
JITTER_POR_CATEGORIA = {
    "Moradia": (0.6, 1.6),
    "Alimentação": (0.6, 1.6),
    "Transporte": (0.5, 1.8),
    "Dívidas": (0.0, 3.0),
    "Saúde": (0.4, 2.2),
    "Educação": (0.3, 2.5),
    "Lazer": (0.4, 2.2),
    "Serviços": (0.4, 3.2),
    "Outras": (0.4, 4.5),
    "Assinaturas": (0.5, 3.5),
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


def run(n_usuarios: int = 1000) -> pd.DataFrame:
    usuarios = []
    for _ in range(n_usuarios):
        usuarios.append(gerar_usuario_bruto())

    return pd.DataFrame(usuarios)