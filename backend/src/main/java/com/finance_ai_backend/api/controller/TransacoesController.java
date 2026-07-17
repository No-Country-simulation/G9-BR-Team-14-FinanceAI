package com.finance_ai_backend.api.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.finance_ai_backend.api.domain.dtos.ClassificacaoRequestDTO;
import com.finance_ai_backend.api.domain.dtos.RetornoClassificacaoDTO;
import com.finance_ai_backend.api.domain.models.Usuario;
import com.finance_ai_backend.api.services.TransacoesService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;


@RestController
@RequestMapping("transacoes")
@SecurityRequirement(name = "bearerAuth") 
public class TransacoesController {
    private final TransacoesService categoriaService;

    public TransacoesController(TransacoesService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @PostMapping("categoria")
    public RetornoClassificacaoDTO verificaCategoria(
        // @AuthenticationPrincipal Usuario usuario,
        @RequestBody ClassificacaoRequestDTO request
    ) {
        return categoriaService.classificar(request.descricao());
    }
}