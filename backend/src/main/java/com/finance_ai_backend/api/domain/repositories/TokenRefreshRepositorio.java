package com.finance_ai_backend.api.domain.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.finance_ai_backend.api.domain.models.TokenRefresh;

@Repository
public interface TokenRefreshRepositorio extends JpaRepository<TokenRefresh, UUID> {
    Optional<TokenRefresh> findByTokenHash(String tokenHash);
}
