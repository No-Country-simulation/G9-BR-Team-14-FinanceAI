package com.finance_ai_backend.api.domain.repositories;


import org.springframework.data.jpa.repository.JpaRepository;

import com.finance_ai_backend.api.domain.models.CategoriaTransacao;

public interface CategoriaTransacaoRepositorio extends JpaRepository<CategoriaTransacao, Long> {
    
}
