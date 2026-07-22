package com.finance_ai_backend.api.domain.dtos;

import java.math.BigDecimal;

import com.finance_ai_backend.api.domain.validations.UsernameUnico;

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
public class UsuarioMinimoParaCadastroDTO {
    @NotBlank(message="O campo username é obrigatório")
    @UsernameUnico
    private String username;
    
    @NotBlank(message="O campo senha é obrigatório")
    private String senha;

    @Builder.Default
    private BigDecimal totalPoupanca = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal totalDividas = BigDecimal.ZERO;
}
