package com.finance_ai_backend.api.domain.dtos;

import java.util.List;

public record PerfilUsuarioRetornoDTO(
    String perfil,
    List<String> sugestoes
) {}
