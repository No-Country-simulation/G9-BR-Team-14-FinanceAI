package com.finance_ai_backend.api.domain.dtos;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SugestoesRetornoDTO(
    @JsonProperty("sugestoes_ativas") List<String> sugestoesAtivas
) {}
