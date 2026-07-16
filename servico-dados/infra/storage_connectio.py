import os
import boto3
from botocore.config import Config

from infra.config import (
    OCI_BUCKET_NAME,
    OCI_PREFIX,
    OCI_NAMESPACE,
    OCI_REGION,
    OCI_S3_ACCESS_KEY,
    OCI_S3_SECRET_KEY
)

class StorageConnection:
    def __init__(self):
        self.endpoint_url = f"https://{OCI_NAMESPACE}.compat.objectstorage.{OCI_REGION}.oraclecloud.com"
        self.cliente = boto3.client(
            "s3",
            endpoint_url=self.endpoint_url,
            aws_access_key_id=OCI_S3_ACCESS_KEY,
            aws_secret_access_key=OCI_S3_SECRET_KEY,
            region_name=OCI_REGION,
            # OCI (e outros provedores S3-compatíveis) não suporta o
            # "aws-chunked" encoding com checksum que o botocore >= 1.36
            # passou a usar por padrão em put_object. Desativamos aqui.
            config=Config(
                request_checksum_calculation="when_required",
                response_checksum_validation="when_required",
            ),
        )

    def obtem_item(self, nome_objeto):
        pasta_modelos='ia_modelos'
        chave_completa = f"{OCI_PREFIX}/{nome_objeto}"

        destino = os.path.join(pasta_modelos, nome_objeto)
        os.makedirs(os.path.dirname(destino), exist_ok=True)
        self.cliente.download_file(OCI_BUCKET_NAME, chave_completa, destino)
