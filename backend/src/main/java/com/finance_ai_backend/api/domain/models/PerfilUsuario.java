package com.finance_ai_backend.api.domain.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "perfil_usuario")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerfilUsuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "pk", nullable = false, updatable = false)
    private UUID pk;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_pk", nullable = false)
    private Usuario usuario;

    @Column(name = "pefil_categorizado")
    private String perfilCategorizado;

    @Builder.Default
    @Column(name = "sugestoes_perfil_usuario")
    private List<SugestoesPerfilUsuarioEnum> sugestoePerfilUsuario  =  new ArrayList<SugestoesPerfilUsuarioEnum>();

}

