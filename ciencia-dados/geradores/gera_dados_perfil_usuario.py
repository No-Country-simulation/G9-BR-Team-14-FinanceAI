import random
import pandas as pd

random.seed()

CATEGORIAS = [
    "Alimentação", "Transporte", "Saúde", "Moradia", "Educação",
    "Lazer", "Serviços", "Assinaturas", "Dívidas", "Outras",
]

RENDA_MIN = 1500
RENDA_MAX = 20000


def gerar_usuario_bruto() -> dict:
    renda_mensal = round(random.uniform(RENDA_MIN, RENDA_MAX), 2)

    # Valor investido: varia bastante entre usuários (alguns não investem nada,
    # outros investem uma fatia relevante da renda) para gerar diversidade
    # suficiente entre perfis "saudáveis" e "em risco" depois.
    percentual_investido_base = random.choice([
        random.uniform(0, 0.05),   # quase não investe
        random.uniform(0.05, 0.15),  # investe pouco
        random.uniform(0.15, 0.35),  # investe bem
    ])
    valor_investido = round(renda_mensal * percentual_investido_base, 2)

    # Gasto por categoria: cada usuário tem um "perfil de consumo" aleatório,
    # simulando desde quem gasta pouco até quem gasta quase tudo (ou mais)
    # que a renda.
    percentual_gasto_total_base = random.choice([
        random.uniform(0.3, 0.6),   # gasta pouco da renda
        random.uniform(0.6, 0.9),   # gasta moderado/alto
        random.uniform(0.9, 1.3),   # gasta quase tudo ou mais que a renda
    ])
    gasto_total_alvo = renda_mensal * percentual_gasto_total_base

    # Distribui o gasto total entre as categorias de forma aleatória
    # (pesos aleatórios que somam 1, multiplicados pelo gasto total alvo)
    pesos = [random.random() for _ in CATEGORIAS]
    soma_pesos = sum(pesos)
    pesos_normalizados = [p / soma_pesos for p in pesos]

    linha = {
        "renda_mensal": renda_mensal,
        "valor_investido": valor_investido,
    }

    for categoria, peso in zip(CATEGORIAS, pesos_normalizados):
        nome_categoria = f'gasto_{categoria.lower()}'
        linha[nome_categoria] = round(gasto_total_alvo * peso, 2)

    return linha


def run(n_usuarios: int = 1000) -> pd.DataFrame:
    usuarios = []
    for _ in range(n_usuarios):
        usuarios.append(gerar_usuario_bruto())

    return pd.DataFrame(usuarios)
