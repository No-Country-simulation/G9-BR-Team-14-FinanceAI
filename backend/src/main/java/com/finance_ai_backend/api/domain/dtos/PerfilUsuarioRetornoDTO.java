package com.finance_ai_backend.api.domain.dtos;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

public record PerfilUsuarioRetornoDTO(
    @Schema(description = "Perfil financeiro categorizado do usuário", example = "Economizador")
    String perfil,

    @Schema(description = "Lista de sugestões personalizadas para o usuário")
    List<String> sugestoes
) {}
