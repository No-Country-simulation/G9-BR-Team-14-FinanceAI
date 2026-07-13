package com.finance_ai_backend.api.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.finance_ai_backend.api.domain.dtos.TokenBlacklistDTO;
import com.finance_ai_backend.api.domain.dtos.TokenGeracaoDTO;
import com.finance_ai_backend.api.domain.dtos.TokenRefreshDTO;
import com.finance_ai_backend.api.domain.dtos.TokenRespostaDTO;
import com.finance_ai_backend.api.services.TokenService;

import io.swagger.v3.oas.annotations.security.SecurityRequirements;

@RestController
@RequestMapping("token")
@SecurityRequirements
public class TokenController {
    private final TokenService tokenService;

    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping()
    public TokenRespostaDTO gerarToken(@RequestBody @Valid TokenGeracaoDTO dto) {
        return tokenService.gerarTokens(dto);
    }

    @PostMapping("refresh")
    public TokenRespostaDTO refreshToken(@RequestBody @Valid TokenRefreshDTO dto) {
        return tokenService.refreshTokens(dto);
    }

    @PostMapping("blacklist")
    public String blacklistToken(@RequestBody @Valid TokenBlacklistDTO dto) {
        tokenService.blacklistToken(dto);
        return "logout realizado";
    }
}

