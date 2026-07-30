from sklearn.base import BaseEstimator, TransformerMixin
import numpy as np

class FastTextVectorizer(BaseEstimator, TransformerMixin):
    def __init__(self, model, dim=300):
        self.model = model
        self.dim = dim
    
    def fit(self, X, y=None):
        return self
    
    def transform(self, X):
        vetores = []
        for texto in X:
            palavras = texto.lower().split()
            vetores_palavras = []
            for palavra in palavras:
                if palavra in self.model:
                    vetores_palavras.append(self.model[palavra])
            
            if vetores_palavras:
                vetor = np.mean(vetores_palavras, axis=0)
            else:
                vetor = np.zeros(self.dim)
            
            vetores.append(vetor)
        
        return np.array(vetores)
