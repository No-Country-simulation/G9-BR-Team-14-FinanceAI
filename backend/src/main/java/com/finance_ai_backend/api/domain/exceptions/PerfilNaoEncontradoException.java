package com.finance_ai_backend.api.domain.exceptions;

public class PerfilNaoEncontradoException extends RuntimeException {
    public PerfilNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
