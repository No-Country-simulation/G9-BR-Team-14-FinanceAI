package com.finance_ai_backend.api.mappers;

import com.finance_ai_backend.api.domain.dtos.TokenRespostaDTO;

public class TokenMapper {
    public static TokenRespostaDTO paraTokenRespostaDTO(
            String accessTokenJwt,
            String refreshTokenJwt
           ) {
        return TokenRespostaDTO.builder()
                .accessToken(accessTokenJwt)
                .refreshToken(refreshTokenJwt)
                .build();
    }
}
