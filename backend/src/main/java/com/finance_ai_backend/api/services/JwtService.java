package com.finance_ai_backend.api.services;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HexFormat;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.finance_ai_backend.api.domain.exceptions.TokenInvalidoException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

@Service
public class JwtService {

    public static final String TIPO_ACESSO = "ACCESS";
    public static final String TIPO_REFRESH = "REFRESH";

    private final SecretKey chaveAssinatura;
    private final long duracaoAcessoMinutos;
    private final long duracaoRefreshDias;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token.duracao-minutos}") long duracaoAcessoMinutos,
            @Value("${jwt.refresh-token.duracao-dias}") long duracaoRefreshDias) {
        this.chaveAssinatura = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.duracaoAcessoMinutos = duracaoAcessoMinutos;
        this.duracaoRefreshDias = duracaoRefreshDias;
    }

    public Duration duracaoAcesso() {
        return Duration.ofMinutes(duracaoAcessoMinutos);
    }

    public Duration duracaoRefresh() {
        return Duration.ofDays(duracaoRefreshDias);
    }

    public String gerarToken(UUID usuarioId, String tipo, Instant emitidoEm, Instant expiraEm) {
        return Jwts.builder()
                .subject(usuarioId.toString())
                .claim("tipo", tipo)
                .id(UUID.randomUUID().toString())
                .issuedAt(Date.from(emitidoEm))
                .expiration(Date.from(expiraEm))
                .signWith(chaveAssinatura)
                .compact();
    }

    public LocalDateTime paraLocalDateTime(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    public Claims validarEExtrairClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(chaveAssinatura)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            throw new TokenInvalidoException("Token inválido ou expirado");
        }
    }

    public String sha256Hash(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algoritmo SHA-256 indisponível", e);
        }
    }
}
