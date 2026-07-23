package com.finance_ai_backend.api.mappers;

import com.finance_ai_backend.api.domain.dtos.UsuarioMinimoParaCadastroDTO;
import com.finance_ai_backend.api.domain.models.Usuario;

public class UsuarioMapper {
    public static Usuario UsuarioMinimoParaCadastroToEntity(UsuarioMinimoParaCadastroDTO usuarioMinimoParaCadastroDTO){
        return Usuario.builder()
        .username(usuarioMinimoParaCadastroDTO.getUsername())
        .senhaHash(usuarioMinimoParaCadastroDTO.getSenha())
        .build();
    }
}
