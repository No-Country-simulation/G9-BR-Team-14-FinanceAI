package com.finance_ai_backend.api.domain.models;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tokens_acesso")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenAcesso {

    /**
     * O próprio valor do token (UUID simples) é usado como identificador,
     * gerado pela aplicação em {@code TokenService} — não é auto-gerado pelo JPA.
     */
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_usuario", nullable = false, foreignKey = @ForeignKey(name = "fk_usuario_token_ref"))
    private Usuario usuario;

    @Column(name = "emitido_em")
    private LocalDateTime emitidoEm;

    @Column(name = "expira_em")
    private LocalDateTime expiraEm;

    @Column(name = "valido")
    private Boolean valido;
}
