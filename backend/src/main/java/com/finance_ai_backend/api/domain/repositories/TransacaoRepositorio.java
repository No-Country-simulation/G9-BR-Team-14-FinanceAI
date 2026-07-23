package com.finance_ai_backend.api.domain.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.finance_ai_backend.api.domain.dtos.CategoriaTotalDTO;
import com.finance_ai_backend.api.domain.models.Transacao;

public interface TransacaoRepositorio extends JpaRepository<Transacao, Integer> {
    @Query("""
        SELECT t.categoria AS categoria, SUM(t.valor) AS total
        FROM Transacao t
        WHERE t.usuario.pk = :usuarioPk
        GROUP BY t.categoria
    """)
    List<CategoriaTotalDTO> somarPorCategoria(@Param("usuarioPk") UUID usuarioPk);
}
