package com.finance_ai_backend.api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.finance_ai_backend.api.domain.dtos.PerfilUsuarioRetornoDTO;
import com.finance_ai_backend.api.domain.dtos.TransacaoInputDTO;
import com.finance_ai_backend.api.domain.models.Usuario;
import com.finance_ai_backend.api.services.TransacoesService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;



@RestController
@RequestMapping("api/v1/")
@SecurityRequirement(name = "bearerAuth") 
public class TransacoesController {
    private final TransacoesService transacoesService;

    public TransacoesController(TransacoesService transacoesService) {
        this.transacoesService = transacoesService;
    }

    @PostMapping("transacoes")
    public ResponseEntity<?> gerarTrasacoesEmLote(
        @Valid @RequestBody List<TransacaoInputDTO> transacaoInputDTOs,
        @AuthenticationPrincipal Usuario usuario 
    ) {
        transacoesService.salvarTransacoes(
            usuario,
            transacaoInputDTOs
        );
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("analisar")
    public ResponseEntity<?> gerarAnaliseDePerfil(
        @AuthenticationPrincipal Usuario usuario 
    ) {
        transacoesService.executarAnaliseFinanceira(usuario);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("perfil")
    public ResponseEntity<PerfilUsuarioRetornoDTO> buscarPerfil(
        @AuthenticationPrincipal Usuario usuario
    ) {
        return ResponseEntity.ok(transacoesService.buscarPerfil(usuario));
    }
    

}