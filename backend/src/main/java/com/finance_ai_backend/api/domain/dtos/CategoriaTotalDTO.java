package com.finance_ai_backend.api.domain.dtos;

import java.math.BigDecimal;

import com.finance_ai_backend.api.domain.models.CategoriaEnum;

public record CategoriaTotalDTO(CategoriaEnum categoria, BigDecimal total) {}
