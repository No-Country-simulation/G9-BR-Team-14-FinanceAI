package com.finance_ai_backend.api.services;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finance_ai_backend.api.domain.dtos.UsuarioMinimoParaCadastroDTO;
import com.finance_ai_backend.api.domain.models.PerfilUsuario;
import com.finance_ai_backend.api.domain.models.Usuario;
import com.finance_ai_backend.api.domain.repositories.PerfilUsuarioRepositorio;
import com.finance_ai_backend.api.domain.repositories.UsuarioRepositorio;
import com.finance_ai_backend.api.mappers.UsuarioMapper;

@Service
public class UsuarioService {
    private final UsuarioRepositorio repositorio;
    private final PerfilUsuarioRepositorio perfilUsuarioRepositorio;
    private final Argon2PasswordEncoder passwordEncoder;
    
    public UsuarioService(
        UsuarioRepositorio repositorio,
        PerfilUsuarioRepositorio perfilUsuarioRepositorio,
        Argon2PasswordEncoder passwordEncoder
    ) {
        this.repositorio = repositorio;
        this.perfilUsuarioRepositorio = perfilUsuarioRepositorio;
        this.passwordEncoder = passwordEncoder;

    }

    @Transactional
    public Boolean cadastraUsuario(UsuarioMinimoParaCadastroDTO usuarioParaCadastroDTO){
        String hash = this.passwordEncoder.encode(usuarioParaCadastroDTO.getSenha());
        usuarioParaCadastroDTO.setSenha(hash);

        Usuario usuario = this.repositorio.save(UsuarioMapper.UsuarioMinimoParaCadastroToEntity(usuarioParaCadastroDTO));
        this.perfilUsuarioRepositorio.save(
            PerfilUsuario.builder()
            .usuario(usuario)
            .build()
        );

        return true;

    }

}
