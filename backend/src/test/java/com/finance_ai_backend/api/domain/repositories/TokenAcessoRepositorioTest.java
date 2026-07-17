package com.finance_ai_backend.api.domain.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import com.finance_ai_backend.api.domain.models.TokenAcesso;
import com.finance_ai_backend.api.domain.models.Usuario;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TokenAcessoRepositorioTest {

    @Autowired
    private TokenAcessoRepositorio tokenAcessoRepositorio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    private Usuario novoUsuario(String username, String email) {
        return usuarioRepositorio.save(Usuario.builder()
                .username(username)
                .email(email)
                .senhaHash("hash-fake")
                .ativo(true)
                .build());
    }

    private TokenAcesso novoToken(Usuario usuario, boolean valido, LocalDateTime expiraEm) {
        LocalDateTime emitidoEm = LocalDateTime.now();
        return TokenAcesso.builder()
                .id(UUID.randomUUID())
                .usuario(usuario)
                .emitidoEm(emitidoEm)
                .expiraEm(expiraEm)
                .valido(valido)
                .build();
    }

    @Test
    void deveSalvarEEncontrarPorId() {
        Usuario usuario = novoUsuario("joao.silva", "joao@teste.com");
        TokenAcesso salvo = tokenAcessoRepositorio.save(
                novoToken(usuario, true, LocalDateTime.now().plusHours(1)));

        Optional<TokenAcesso> encontrado = tokenAcessoRepositorio.findById(salvo.getId());

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getUsuario().getPk()).isEqualTo(usuario.getPk());
        assertThat(encontrado.get().getValido()).isTrue();
    }

    @Test
    void deveRetornarVazioParaTokenInexistente() {
        assertThat(tokenAcessoRepositorio.findById(UUID.randomUUID())).isEmpty();
    }

    @Test
    void deveMarcarTokenComoInvalidoAoRevogar() {
        Usuario usuario = novoUsuario("maria.souza", "maria@teste.com");
        TokenAcesso salvo = tokenAcessoRepositorio.save(
                novoToken(usuario, true, LocalDateTime.now().plusHours(1)));

        salvo.setValido(false);
        tokenAcessoRepositorio.save(salvo);

        Optional<TokenAcesso> encontrado = tokenAcessoRepositorio.findById(salvo.getId());

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getValido()).isFalse();
    }

    @Test
    void devePersistirDataDeExpiracaoDoToken() {
        Usuario usuario = novoUsuario("carlos.lima", "carlos@teste.com");
        LocalDateTime expiraEm = LocalDateTime.now().plusHours(1);
        TokenAcesso salvo = tokenAcessoRepositorio.save(novoToken(usuario, true, expiraEm));

        Optional<TokenAcesso> encontrado = tokenAcessoRepositorio.findById(salvo.getId());

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getExpiraEm()).isEqualToIgnoringNanos(expiraEm);
    }
}
