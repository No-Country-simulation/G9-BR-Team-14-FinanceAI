package com.finance_ai_backend.api.mappers;

import com.finance_ai_backend.api.domain.dtos.TokenRespostaDTO;

public class TokenMapper {
    public static TokenRespostaDTO paraTokenRespostaDTO(String token) {
        return TokenRespostaDTO.builder()
                .token(token)
                .build();
    }
}
