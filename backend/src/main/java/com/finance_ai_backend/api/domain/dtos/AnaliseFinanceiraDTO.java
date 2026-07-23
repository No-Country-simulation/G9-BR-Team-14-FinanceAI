package com.finance_ai_backend.api.domain.dtos;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AnaliseFinanceiraDTO {

    @JsonProperty("renda_mensal")
    @Builder.Default
    private BigDecimal rendaMensal = BigDecimal.ZERO;

    @JsonProperty("valor_investido")
    @Builder.Default
    private BigDecimal valorInvestido = BigDecimal.ZERO;

    @JsonProperty("gasto_alimentacao")
    @Builder.Default
    private BigDecimal gastoAlimentacao = BigDecimal.ZERO;

    @JsonProperty("gasto_transporte")
    @Builder.Default
    private BigDecimal gastoTransporte = BigDecimal.ZERO;

    @JsonProperty("gasto_saude")
    @Builder.Default
    private BigDecimal gastoSaude = BigDecimal.ZERO;

    @JsonProperty("gasto_moradia")
    @Builder.Default
    private BigDecimal gastoMoradia = BigDecimal.ZERO;

    @JsonProperty("gasto_educacao")
    @Builder.Default
    private BigDecimal gastoEducacao = BigDecimal.ZERO;

    @JsonProperty("gasto_lazer")
    @Builder.Default
    private BigDecimal gastoLazer = BigDecimal.ZERO;

    @JsonProperty("gasto_servicos")
    @Builder.Default
    private BigDecimal gastoServicos = BigDecimal.ZERO;

    @JsonProperty("gasto_assinaturas")
    @Builder.Default
    private BigDecimal gastoAssinaturas = BigDecimal.ZERO;

    @JsonProperty("gasto_dividas")
    @Builder.Default
    private BigDecimal gastoDividas = BigDecimal.ZERO;

    @JsonProperty("gasto_outras")
    @Builder.Default
    private BigDecimal gastoOutras = BigDecimal.ZERO;
}
