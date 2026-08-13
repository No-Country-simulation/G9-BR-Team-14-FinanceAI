package com.finance_ai_backend.api.domain.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "Login do usuário cadastrado", example = "username")
    @NotBlank(message = "O campo login é obrigatório")
    private String login;

    @Schema(description = "Senha do usuário", example = "minhaSenha123")
    @NotBlank(message = "O campo senha é obrigatório")
    private String senha;
}
