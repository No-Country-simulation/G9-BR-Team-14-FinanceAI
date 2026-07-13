package com.finance_ai_backend.api.infra;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.finance_ai_backend.api.domain.exceptions.TokenInvalidoException;
import com.finance_ai_backend.api.services.JwtService;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String PREFIXO_BEARER = "Bearer ";

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String cabecalho = request.getHeader("Authorization");

        if (cabecalho != null && cabecalho.startsWith(PREFIXO_BEARER)) {
            String token = cabecalho.substring(PREFIXO_BEARER.length());
            autenticarSePossivel(token);
        }

        filterChain.doFilter(request, response);
    }

    private void autenticarSePossivel(String token) {
        try {
            Claims claims = jwtService.validarEExtrairClaims(token);
            if (!JwtService.TIPO_ACESSO.equals(claims.get("tipo", String.class))) {
                return;
            }

            UUID usuarioId = UUID.fromString(claims.getSubject());
            UsernamePasswordAuthenticationToken autenticacao = new UsernamePasswordAuthenticationToken(usuarioId, null, List.of());
            SecurityContextHolder.getContext().setAuthentication(autenticacao);
        } catch (TokenInvalidoException | IllegalArgumentException e) {
            // token ausente/expirado/assinatura inválida/subject malformado: segue sem autenticar
        }
    }
}
