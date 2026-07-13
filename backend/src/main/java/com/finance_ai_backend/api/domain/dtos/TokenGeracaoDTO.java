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
public class TokenGeracaoDTO {
    @NotBlank(message = "O campo login é obrigatório")
    private String login;

    @NotBlank(message = "O campo senha é obrigatório")
    private String senha;
}
