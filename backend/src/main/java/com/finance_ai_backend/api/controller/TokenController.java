package com.finance_ai_backend.api.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.finance_ai_backend.api.domain.dtos.TokenGeracaoDTO;
import com.finance_ai_backend.api.domain.dtos.TokenRespostaDTO;
import com.finance_ai_backend.api.domain.exceptions.TokenInvalidoException;
import com.finance_ai_backend.api.services.TokenService;

import io.swagger.v3.oas.annotations.security.SecurityRequirements;

@RestController
@RequestMapping("token")
@SecurityRequirements
public class TokenController {

    private static final String PREFIXO_BEARER = "Bearer ";

    private final TokenService tokenService;

    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping()
    public TokenRespostaDTO gerarToken(@RequestBody @Valid TokenGeracaoDTO dto) {
        return tokenService.gerarToken(dto);
    }

    @PostMapping("blacklist")
    public String blacklistToken(@RequestHeader("Authorization") String cabecalhoAutorizacao) {
        if (cabecalhoAutorizacao == null || !cabecalhoAutorizacao.startsWith(PREFIXO_BEARER)) {
            throw new TokenInvalidoException("Header Authorization ausente ou inválido");
        }

        String token = cabecalhoAutorizacao.substring(PREFIXO_BEARER.length());
        tokenService.revogarToken(token);
        return "logout realizado";
    }
}
