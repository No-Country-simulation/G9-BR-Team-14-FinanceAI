package com.finance_ai_backend.api.services;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finance_ai_backend.api.domain.dtos.TokenGeracaoDTO;
import com.finance_ai_backend.api.domain.dtos.TokenRespostaDTO;
import com.finance_ai_backend.api.domain.exceptions.CredenciaisInvalidasException;
import com.finance_ai_backend.api.domain.exceptions.TokenInvalidoException;
import com.finance_ai_backend.api.domain.models.TokenAcesso;
import com.finance_ai_backend.api.domain.models.Usuario;
import com.finance_ai_backend.api.domain.repositories.TokenAcessoRepositorio;
import com.finance_ai_backend.api.domain.repositories.UsuarioRepositorio;
import com.finance_ai_backend.api.mappers.TokenMapper;

@Service
public class TokenService {

    private static final String MENSAGEM_CREDENCIAIS_INVALIDAS = "Login ou senha inválidos";
    private static final String MENSAGEM_TOKEN_INVALIDO = "Token inválido, expirado ou revogado";

    private final UsuarioRepositorio usuarioRepositorio;
    private final TokenAcessoRepositorio tokenAcessoRepositorio;
    private final Argon2PasswordEncoder passwordEncoder;
    private final Duration duracaoToken;

    public TokenService(
            UsuarioRepositorio usuarioRepositorio,
            TokenAcessoRepositorio tokenAcessoRepositorio,
            Argon2PasswordEncoder passwordEncoder,
            @Value("${auth.token.duracao-horas}") long duracaoHoras
        ) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.tokenAcessoRepositorio = tokenAcessoRepositorio;
        this.passwordEncoder = passwordEncoder;
        this.duracaoToken = Duration.ofHours(duracaoHoras);
    }

    @Transactional
    public TokenRespostaDTO gerarToken(TokenGeracaoDTO dto) {
        Usuario usuario = usuarioRepositorio.findByUsername(dto.getLogin())
               .orElseThrow(() -> {
                    return new CredenciaisInvalidasException(MENSAGEM_CREDENCIAIS_INVALIDAS);
                });

        if (!passwordEncoder.matches(dto.getSenha(), usuario.getSenhaHash())) {
            throw new CredenciaisInvalidasException(MENSAGEM_CREDENCIAIS_INVALIDAS);
        }

        LocalDateTime emitidoEm = LocalDateTime.now();
        UUID token = UUID.randomUUID();

        tokenAcessoRepositorio.save(
            TokenAcesso.builder()
                .id(token)
                .usuario(usuario)
                .emitidoEm(emitidoEm)
                .expiraEm(emitidoEm.plus(duracaoToken))
                .valido(true)
                .build()
        );

        return TokenMapper.paraTokenRespostaDTO(token.toString());
    }

    @Transactional
    public Usuario validarToken(String token) {
        UUID tokenId = parseToken(token);

        TokenAcesso tokenAcesso = tokenAcessoRepositorio.findById(tokenId)
                .orElseThrow(() -> new TokenInvalidoException(MENSAGEM_TOKEN_INVALIDO));

        if (!Boolean.TRUE.equals(tokenAcesso.getValido())
                || tokenAcesso.getExpiraEm() == null
                || tokenAcesso.getExpiraEm().isBefore(LocalDateTime.now())) {
            throw new TokenInvalidoException(MENSAGEM_TOKEN_INVALIDO);
        }

        return tokenAcesso.getUsuario();
    }

    @Transactional
    public void revogarToken(String token) {
        UUID tokenId = parseToken(token);

        tokenAcessoRepositorio.findById(tokenId).ifPresent(tokenAcesso -> {
            tokenAcesso.setValido(false);
            tokenAcessoRepositorio.save(tokenAcesso);
        });
    }

    private UUID parseToken(String token) {
        try {
            return UUID.fromString(token);
        } catch (IllegalArgumentException e) {
            throw new TokenInvalidoException(MENSAGEM_TOKEN_INVALIDO);
        }
    }
}
