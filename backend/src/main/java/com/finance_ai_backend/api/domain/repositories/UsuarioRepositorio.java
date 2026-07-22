package com.finance_ai_backend.api.domain.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.finance_ai_backend.api.domain.models.Usuario;

public interface UsuarioRepositorio extends JpaRepository<Usuario, UUID> {
    Optional<Usuario> findByUsername(String username);
    boolean existsByUsername(String username);
}
