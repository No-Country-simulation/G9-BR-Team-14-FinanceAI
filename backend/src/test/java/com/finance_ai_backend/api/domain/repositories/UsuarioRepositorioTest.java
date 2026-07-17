package com.finance_ai_backend.api.domain.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;
import java.util.UUID;

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

    private Usuario novoUsuario(String username, String email) {
        return Usuario.builder()
                .username(username)
                .email(email)
                .senhaHash("hash-fake")
                .ativo(true)
                .build();
    }

    @Test
    void deveSalvarEEncontrarPorUsername() {
        Usuario salvo = usuarioRepositorio.save(novoUsuario("joao.silva", "joao@teste.com"));

        Optional<Usuario> encontrado = usuarioRepositorio.findByUsername("joao.silva");

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getPk()).isEqualTo(salvo.getPk());
    }

    @Test
    void deveEncontrarPorEmail() {
        usuarioRepositorio.save(novoUsuario("maria.souza", "maria@teste.com"));

        Optional<Usuario> encontrado = usuarioRepositorio.findByEmail("maria@teste.com");

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getUsername()).isEqualTo("maria.souza");
    }

    @Test
    void deveEncontrarPorUsernameOuEmail() {
        usuarioRepositorio.save(novoUsuario("carlos.lima", "carlos@teste.com"));

        assertThat(usuarioRepositorio.findByUsernameOrEmail("carlos.lima", "carlos.lima")).isPresent();
        assertThat(usuarioRepositorio.findByUsernameOrEmail("carlos@teste.com", "carlos@teste.com")).isPresent();
        assertThat(usuarioRepositorio.findByUsernameOrEmail("inexistente", "inexistente")).isEmpty();
    }

    @Test
    void existsByEmailEExistsByUsername_devemRefletirEstadoDoBanco() {
        usuarioRepositorio.save(novoUsuario("ana.paula", "ana@teste.com"));

        assertThat(usuarioRepositorio.existsByEmail("ana@teste.com")).isTrue();
        assertThat(usuarioRepositorio.existsByUsername("ana.paula")).isTrue();
        assertThat(usuarioRepositorio.existsByEmail("naoexiste@teste.com")).isFalse();
        assertThat(usuarioRepositorio.existsByUsername(UUID.randomUUID().toString())).isFalse();
    }

    @Test
    void deveLancarExcecaoAoSalvarEmailDuplicado() {
        usuarioRepositorio.saveAndFlush(novoUsuario("pedro.alves", "duplicado@teste.com"));

        assertThatThrownBy(() ->
                usuarioRepositorio.saveAndFlush(novoUsuario("outro.usuario", "duplicado@teste.com"))
        ).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void deveLancarExcecaoAoSalvarUsernameDuplicado() {
        usuarioRepositorio.saveAndFlush(novoUsuario("username.duplicado", "primeiro@teste.com"));

        assertThatThrownBy(() ->
                usuarioRepositorio.saveAndFlush(novoUsuario("username.duplicado", "segundo@teste.com"))
        ).isInstanceOf(DataIntegrityViolationException.class);
    }
}
