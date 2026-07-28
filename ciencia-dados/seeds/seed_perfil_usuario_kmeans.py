import numpy as np
import pandas as pd

COLUNAS = [
    "ALIMENTACAO", "TRANSPORTE", "SAUDE", "MORADIA", "EDUCACAO",
    "LAZER", "SERVICOS", "ASSINATURAS", "DIVIDAS", "POUPANCA"
]


def gerar_linha(soma_alvo):
    """Gera 10 valores aleatórios (float, 2 casas decimais) que somam soma_alvo."""
    pesos = np.random.dirichlet(np.ones(len(COLUNAS)))
    valores = np.round(pesos * soma_alvo, 2)
    valores[0] += round(soma_alvo - valores.sum(), 2)
    return valores


def gerar_tabela(numero_linhas_iguais=6, numero_linhas_abaixo=2, numero_linhas_acima=2, pct_minima=60, pct_maxima=120, seed=None):
    np.random.seed(seed)

    linhas = []

    for _ in range(numero_linhas_iguais):
        linhas.append(gerar_linha(100))

    for _ in range(numero_linhas_abaixo):
        linhas.append(gerar_linha(np.random.uniform(pct_minima, 100)))

    for _ in range(numero_linhas_acima):
        linhas.append(gerar_linha(np.random.uniform(100, pct_maxima)))

    df = pd.DataFrame(linhas, columns=COLUNAS)

    return df.sample(frac=1, random_state=seed).reset_index(drop=True)
