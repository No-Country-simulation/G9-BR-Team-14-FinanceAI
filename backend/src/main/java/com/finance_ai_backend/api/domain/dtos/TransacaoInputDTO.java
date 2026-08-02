package com.finance_ai_backend.api.domain.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import io.swagger.v3.oas.annotations.media.Schema;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TransacaoInputDTO {
    @Schema(description = "Descrição da transação", example = "Supermercado")
    @NotBlank(message = "Esse item é necessário")
    private String descricao;

    @Schema(description = "Data em que a transação ocorreu", example = "2026-07-15")
    @NotNull(message = "A data é necessária")
    @PastOrPresent(message = "A data não deve estar no futuro")
    private LocalDate data;

    @Schema(description = "Valor da transação", example = "150.00")
    @NotNull(message = "Esse valor é necessário")
    @DecimalMin(value = "0.01", message = "Você deve ter um gasto mínimo")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal valor;
}
