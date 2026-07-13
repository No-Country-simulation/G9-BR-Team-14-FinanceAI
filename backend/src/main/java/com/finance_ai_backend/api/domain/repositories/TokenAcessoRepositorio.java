package com.finance_ai_backend.api.domain.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.finance_ai_backend.api.domain.models.TokenAcesso;

@Repository
public interface TokenAcessoRepositorio extends JpaRepository<TokenAcesso, UUID> {
    Optional<TokenAcesso> findByTokenHash(String tokenHash);
}
