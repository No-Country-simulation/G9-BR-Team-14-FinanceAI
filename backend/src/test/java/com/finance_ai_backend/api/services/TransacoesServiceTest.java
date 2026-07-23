package com.finance_ai_backend.api.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

import com.finance_ai_backend.api.domain.dtos.PerfilUsuarioRetornoDTO;
import com.finance_ai_backend.api.domain.exceptions.PerfilNaoEncontradoException;
import com.finance_ai_backend.api.domain.models.PerfilUsuario;
import com.finance_ai_backend.api.domain.models.Usuario;
import com.finance_ai_backend.api.domain.repositories.PerfilUsuarioRepositorio;
import com.finance_ai_backend.api.domain.repositories.TransacaoRepositorio;

@ExtendWith(MockitoExtension.class)
class TransacoesServiceTest {

    @Mock
    private TransacaoRepositorio transacaoRepositorio;

    @Mock
    private PerfilUsuarioRepositorio perfilUsuarioRepositorio;

    @Mock
    private RestClient.Builder restClientBuilder;

    private TransacoesService transacoesService;

    @BeforeEach
    void setUp() {
        lenient().when(restClientBuilder.baseUrl(anyString())).thenReturn(restClientBuilder);
        lenient().when(restClientBuilder.requestFactory(any())).thenReturn(restClientBuilder);

        transacoesService = new TransacoesService(
            restClientBuilder,
            transacaoRepositorio,
            perfilUsuarioRepositorio,
            "http://predict-api",
            "X-API-KEY",
            "chave-fake"
        );
    }

    private Usuario novoUsuario() {
        return Usuario.builder()
            .pk(UUID.randomUUID())
            .username("usuario.teste")
            .senhaHash("hash-fake")
            .build();
    }

    @Test
    void deveRetornarPerfilQuandoEncontrado() {
        Usuario usuario = novoUsuario();
        PerfilUsuario perfilUsuario = PerfilUsuario.builder()
            .pk(usuario.getPk())
            .usuario(usuario)
            .perfilCategorizado("conservador")
            .sugestoesPerfilUsuario(List.of("Reduza gastos com lazer", "Aumente a poupança"))
            .build();

        when(perfilUsuarioRepositorio.findById(usuario.getPk())).thenReturn(Optional.of(perfilUsuario));

        PerfilUsuarioRetornoDTO resultado = transacoesService.buscarPerfil(usuario);

        assertThat(resultado.perfil()).isEqualTo("conservador");
        assertThat(resultado.sugestoes()).containsExactly("Reduza gastos com lazer", "Aumente a poupança");
        verify(perfilUsuarioRepositorio).findById(usuario.getPk());
    }

    @Test
    void deveLancarExcecaoQuandoPerfilNaoEncontrado() {
        Usuario usuario = novoUsuario();

        when(perfilUsuarioRepositorio.findById(usuario.getPk())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transacoesService.buscarPerfil(usuario))
            .isInstanceOf(PerfilNaoEncontradoException.class)
            .hasMessage("Perfil não encontrado para o usuário");
    }
}
