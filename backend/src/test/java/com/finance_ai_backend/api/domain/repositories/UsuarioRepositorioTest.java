package com.finance_ai_backend.api.domain.repositories;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException;

import com.finance_ai_backend.api.domain.models.Usuario;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UsuarioRepositorioTest {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    private Usuario novoUsuario(String username) {
        return Usuario.builder()
                .username(username)
                .senhaHash("hash-fake")
                .build();
    }

    @Test
    void deveSalvarEEncontrarPorUsername() {
        Usuario salvo = usuarioRepositorio.save(novoUsuario("joao.silva"));

        Optional<Usuario> encontrado = usuarioRepositorio.findByUsername("joao.silva");

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getPk()).isEqualTo(salvo.getPk());
    }

    @Test
    void deveLancarExcecaoAoSalvarUsernameDuplicado() {
        usuarioRepositorio.saveAndFlush(novoUsuario("username.duplicado"));

        assertThatThrownBy(() ->
                usuarioRepositorio.saveAndFlush(novoUsuario("username.duplicado"))
        ).isInstanceOf(DataIntegrityViolationException.class);
    }
}
