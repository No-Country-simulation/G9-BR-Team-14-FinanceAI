import random
import string
import pandas as pd
from seeds.categorias import categorias_seed

def ruido_inserir(texto: str, numero_caracteres=1):
    resultado = list(texto)
    for _ in range(numero_caracteres):
        resultado.insert(
            random.randrange(0, len(resultado)),
            random.choice(string.ascii_letters)
        )
    return ''.join(resultado)

def ruido_substituir(texto: str, numero_caracteres=1):
    resultado = list(texto)
    for _ in range(numero_caracteres):
        resultado[random.randrange(0, len(resultado))] = random.choice(string.ascii_letters)
    return ''.join(resultado)

def ruido_remover(texto: str, numero_caracteres=1):
    resultado = list(texto)
    for _ in range(min(numero_caracteres, len(resultado))):
        resultado.pop(random.randrange(0, len(resultado)))
    return ''.join(resultado)

def run():
    resultado_final = []
    itens_por_categoria = 1000
    porcentagem_de_itens_ruidosos = 0.2
    ruidos_lista = [
        lambda x: ruido_inserir(x),
        lambda x: ruido_substituir(x),
        lambda x: ruido_remover(x)
    ]

    def aplicar_ruido(descricao):
        ruido = random.choice(ruidos_lista)
        return ruido(descricao)

    for categoria_item in categorias_seed:
        categoria_nome = categoria_item['categoria']
        descricoes     = categoria_item['descricoes']
        valor_minimo   = categoria_item['faixa_valor']['min']
        valor_maximo   = categoria_item['faixa_valor']['max']
        
        for _ in range(itens_por_categoria):
            descricao = random.choice(descricoes)
            valor     = random.randrange(valor_minimo, valor_maximo)
            
            if len(descricao) >=6 and random.random() < porcentagem_de_itens_ruidosos:
                descricao = aplicar_ruido(descricao)

            linha = {
                "categoria" : categoria_nome,
                "descricao" : descricao,
                "valor"     : valor
            }
            resultado_final.append(linha)
    random.shuffle(resultado_final)
    return pd.DataFrame(resultado_final)
