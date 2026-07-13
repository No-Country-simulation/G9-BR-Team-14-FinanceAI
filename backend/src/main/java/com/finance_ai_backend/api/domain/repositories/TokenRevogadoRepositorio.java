package com.finance_ai_backend.api.domain.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.finance_ai_backend.api.domain.models.TokenRevogado;

@Repository
public interface TokenRevogadoRepositorio extends JpaRepository<TokenRevogado, UUID> {
    boolean existsByTokenHash(String tokenHash);
}
