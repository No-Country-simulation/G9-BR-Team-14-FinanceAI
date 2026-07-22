package com.finance_ai_backend.api.domain.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.finance_ai_backend.api.domain.models.TokenAcesso;

public interface TokenAcessoRepositorio extends JpaRepository<TokenAcesso, UUID> {
}
