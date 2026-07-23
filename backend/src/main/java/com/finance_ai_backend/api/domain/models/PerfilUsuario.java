package com.finance_ai_backend.api.domain.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
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
    @Column(name = "pk")
    private UUID pk;

    @OneToOne(fetch = FetchType.EAGER)
    @MapsId
    @JoinColumn(name = "pk")
    private Usuario usuario;

    @Column(name = "perfil_categorizado")
    private String perfilCategorizado;

    @ElementCollection
    @CollectionTable(name = "sugestoes_perfil_usuario", joinColumns = @JoinColumn(name = "perfil_usuario_pk"))
    @Column(name = "sugestao")
    @Builder.Default
    private List<String> sugestoesPerfilUsuario = new ArrayList<>();

}

