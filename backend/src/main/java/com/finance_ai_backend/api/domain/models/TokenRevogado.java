package com.finance_ai_backend.api.domain.models;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.finance_ai_backend.api.domain.enums.MotivoRevogacao;

@Entity
@Table(name = "tokens_revogados")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenRevogado {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_usuario", nullable = false, foreignKey = @ForeignKey(name = "fk_usuario_rev_ref"))
    private Usuario usuario;

    @Column(name = "token_hash")
    private String tokenHash;

    @Column(name = "revogado_em")
    private LocalDateTime revogadoEm;

    @Enumerated(EnumType.STRING)
    @Column(name = "motivo")
    private MotivoRevogacao motivo;
}
