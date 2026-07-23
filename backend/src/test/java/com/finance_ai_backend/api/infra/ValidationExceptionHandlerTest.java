package com.finance_ai_backend.api.infra;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.finance_ai_backend.api.domain.exceptions.PerfilNaoEncontradoException;

class ValidationExceptionHandlerTest {

    private final ValidationExceptionHandler handler = new ValidationExceptionHandler();

    @Test
    void deveRetornar404QuandoPerfilNaoEncontrado() {
        PerfilNaoEncontradoException excecao = new PerfilNaoEncontradoException("Perfil não encontrado para o usuário");

        ResponseEntity<java.util.Map<String, String>> resposta = handler.handlePerfilNaoEncontrado(excecao);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(resposta.getBody()).containsEntry("erro", "Perfil não encontrado para o usuário");
    }
}
