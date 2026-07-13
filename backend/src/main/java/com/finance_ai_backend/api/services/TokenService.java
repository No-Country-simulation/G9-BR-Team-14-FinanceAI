package com.finance_ai_backend.api.services;

import java.time.Instant;
import java.util.UUID;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finance_ai_backend.api.domain.dtos.TokenBlacklistDTO;
import com.finance_ai_backend.api.domain.dtos.TokenGeracaoDTO;
import com.finance_ai_backend.api.domain.dtos.TokenRefreshDTO;
import com.finance_ai_backend.api.domain.dtos.TokenRespostaDTO;
import com.finance_ai_backend.api.domain.enums.MotivoRevogacao;
import com.finance_ai_backend.api.domain.exceptions.CredenciaisInvalidasException;
import com.finance_ai_backend.api.domain.exceptions.TokenInvalidoException;
import com.finance_ai_backend.api.domain.models.TokenAcesso;
import com.finance_ai_backend.api.domain.models.TokenRefresh;
import com.finance_ai_backend.api.domain.models.TokenRevogado;
import com.finance_ai_backend.api.domain.models.Usuario;
import com.finance_ai_backend.api.domain.repositories.TokenAcessoRepositorio;
import com.finance_ai_backend.api.domain.repositories.TokenRefreshRepositorio;
import com.finance_ai_backend.api.domain.repositories.TokenRevogadoRepositorio;
import com.finance_ai_backend.api.domain.repositories.UsuarioRepositorio;
import com.finance_ai_backend.api.mappers.TokenMapper;

import io.jsonwebtoken.Claims;

@Service
public class TokenService {

    private static final String MENSAGEM_CREDENCIAIS_INVALIDAS = "Login ou senha inválidos";
    private static final String MENSAGEM_TOKEN_INVALIDO = "Refresh token inválido, expirado ou já revogado";

    private final UsuarioRepositorio usuarioRepositorio;
    private final TokenAcessoRepositorio tokenAcessoRepositorio;
    private final TokenRefreshRepositorio tokenRefreshRepositorio;
    private final TokenRevogadoRepositorio tokenRevogadoRepositorio;
    private final Argon2PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public TokenService(
            UsuarioRepositorio usuarioRepositorio,
            TokenAcessoRepositorio tokenAcessoRepositorio,
            TokenRefreshRepositorio tokenRefreshRepositorio,
            TokenRevogadoRepositorio tokenRevogadoRepositorio,
            Argon2PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.tokenAcessoRepositorio = tokenAcessoRepositorio;
        this.tokenRefreshRepositorio = tokenRefreshRepositorio;
        this.tokenRevogadoRepositorio = tokenRevogadoRepositorio;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public TokenRespostaDTO gerarTokens(TokenGeracaoDTO dto) {
        Usuario usuario = usuarioRepositorio.findByUsernameOrEmail(dto.getLogin(), dto.getLogin())
                .orElseThrow(() -> new CredenciaisInvalidasException(MENSAGEM_CREDENCIAIS_INVALIDAS));

        if (!Boolean.TRUE.equals(usuario.getAtivo())) {
            throw new CredenciaisInvalidasException(MENSAGEM_CREDENCIAIS_INVALIDAS);
        }

        if (!passwordEncoder.matches(dto.getSenha(), usuario.getSenhaHash())) {
            throw new CredenciaisInvalidasException(MENSAGEM_CREDENCIAIS_INVALIDAS);
        }

        TokenRespostaDTO resposta = emitirParDeTokens(usuario);
        usuario.setUltimoLoginEm(Instant.now().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime());
        usuarioRepositorio.save(usuario);
        return resposta;
    }

    @Transactional
    public TokenRespostaDTO refreshTokens(TokenRefreshDTO dto) {
        Claims claims = jwtService.validarEExtrairClaims(dto.getRefreshToken());
        if (!JwtService.TIPO_REFRESH.equals(claims.get("tipo", String.class))) {
            throw new TokenInvalidoException(MENSAGEM_TOKEN_INVALIDO);
        }

        String hash = jwtService.sha256Hash(dto.getRefreshToken());
        TokenRefresh tokenRefresh = tokenRefreshRepositorio.findByTokenHash(hash)
                .filter(t -> Boolean.TRUE.equals(t.getValido()))
                .orElseThrow(() -> new TokenInvalidoException(MENSAGEM_TOKEN_INVALIDO));

        tokenRefresh.setValido(false);
        tokenRefreshRepositorio.save(tokenRefresh);

        registrarRevogacao(tokenRefresh.getUsuario(), hash, MotivoRevogacao.REFRESH);

        Usuario usuario = tokenRefresh.getUsuario();
        if (!Boolean.TRUE.equals(usuario.getAtivo())) {
            throw new CredenciaisInvalidasException(MENSAGEM_CREDENCIAIS_INVALIDAS);
        }

        return emitirParDeTokens(usuario);
    }

    @Transactional
    public void blacklistToken(TokenBlacklistDTO dto) {
        Claims claims = jwtService.validarEExtrairClaims(dto.getRefreshToken());
        String hash = jwtService.sha256Hash(dto.getRefreshToken());

        if (tokenRevogadoRepositorio.existsByTokenHash(hash)) {
            return;
        }

        Usuario usuario = tokenRefreshRepositorio.findByTokenHash(hash)
                .map(tokenRefresh -> {
                    tokenRefresh.setValido(false);
                    tokenRefreshRepositorio.save(tokenRefresh);
                    return tokenRefresh.getUsuario();
                })
                .orElseGet(() -> usuarioRepositorio.findById(UUID.fromString(claims.getSubject()))
                        .orElseThrow(() -> new TokenInvalidoException(MENSAGEM_TOKEN_INVALIDO)));

        registrarRevogacao(usuario, hash, MotivoRevogacao.LOGOUT);
    }

    private TokenRespostaDTO emitirParDeTokens(Usuario usuario) {
        Instant emitidoEm = Instant.now();
        Instant expiraAcesso = emitidoEm.plus(jwtService.duracaoAcesso());
        Instant expiraRefresh = emitidoEm.plus(jwtService.duracaoRefresh());

        String accessTokenJwt = jwtService.gerarToken(usuario.getPk(), JwtService.TIPO_ACESSO, emitidoEm, expiraAcesso);
        String refreshTokenJwt = jwtService.gerarToken(usuario.getPk(), JwtService.TIPO_REFRESH, emitidoEm, expiraRefresh);

        tokenAcessoRepositorio.save(
            TokenAcesso.builder()
                .usuario(usuario)
                .tokenHash(jwtService.sha256Hash(accessTokenJwt))
                .emitidoEm(jwtService.paraLocalDateTime(emitidoEm))
                .expiraEm(jwtService.paraLocalDateTime(expiraAcesso))
                .valido(true)
                .build()
            );

        tokenRefreshRepositorio.save(TokenRefresh.builder()
                .usuario(usuario)
                .tokenHash(jwtService.sha256Hash(refreshTokenJwt))
                .emitidoEm(jwtService.paraLocalDateTime(emitidoEm))
                .expiraEm(jwtService.paraLocalDateTime(expiraRefresh))
                .valido(true)
                .build()
            );

        return TokenMapper.paraTokenRespostaDTO(accessTokenJwt, refreshTokenJwt);
    }

    private void registrarRevogacao(Usuario usuario, String tokenHash, MotivoRevogacao motivo) {
        tokenRevogadoRepositorio.save(TokenRevogado.builder()
                .usuario(usuario)
                .tokenHash(tokenHash)
                .revogadoEm(java.time.LocalDateTime.now())
                .motivo(motivo)
                .build());
    }
}
