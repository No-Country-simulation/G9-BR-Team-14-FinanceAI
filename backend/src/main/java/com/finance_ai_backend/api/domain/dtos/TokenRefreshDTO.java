package com.finance_ai_backend.api.domain.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TokenRefreshDTO {
    @NotBlank(message = "O campo refreshToken é obrigatório")
    private String refreshToken;
}
