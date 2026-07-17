from os import environ
from dotenv import load_dotenv
 
load_dotenv()

API_KEY_HEADER_NAME = environ['API_KEY_HEADER_NAME'] 
API_KEY = environ['API_KEY']

# --- OCI
OCI_NAMESPACE=environ['OCI_NAMESPACE']
OCI_REGION=environ['OCI_REGION']
OCI_BUCKET_NAME=environ['OCI_BUCKET_NAME']
OCI_S3_ACCESS_KEY=environ['OCI_S3_ACCESS_KEY']
OCI_S3_SECRET_KEY=environ['OCI_S3_SECRET_KEY']
OCI_PREFIX=environ['OCI_PREFIX']

# --- Modelo 1: Classificacao de transacoes ---
MODELO_TRANSACOES=environ['MODELO_TRANSACOES']
VETORIZADOR_TRANSACOES=environ['VETORIZADOR_TRANSACOES']

# --- Modelo 2: Perfil financeiro ---
MODELO_PERFIL=environ['MODELO_PERFIL']
COLUNAS_PERFIL=environ['COLUNAS_PERFIL']

# --- Modelo 3: Sugestoes ---
MODELO_SUGESTOES=environ['MODELO_SUGESTOES']
COLUNAS_SUGESTOES=environ['COLUNAS_SUGESTOES']
NOMES_SUGESTOES=environ['NOMES_SUGESTOES']