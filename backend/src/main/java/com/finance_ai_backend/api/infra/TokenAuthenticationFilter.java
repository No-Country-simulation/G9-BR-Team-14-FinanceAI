package com.finance_ai_backend.api.infra;

import java.io.IOException;
import java.util.List;

import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.finance_ai_backend.api.domain.exceptions.TokenInvalidoException;
import com.finance_ai_backend.api.domain.models.Usuario;
import com.finance_ai_backend.api.services.TokenService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private static final String PREFIXO_BEARER = "Bearer ";

    private final TokenService tokenService;

    public TokenAuthenticationFilter(TokenService tokenService) {
        this.tokenService = tokenService;
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
            Usuario usuario = tokenService.validarToken(token);
            UsernamePasswordAuthenticationToken autenticacao = new UsernamePasswordAuthenticationToken(usuario, null, List.of());
            SecurityContextHolder.getContext().setAuthentication(autenticacao);
        } catch (TokenInvalidoException e) {
            // token ausente/expirado/revogado/formato inválido: segue sem autenticar
        }
    }
}
