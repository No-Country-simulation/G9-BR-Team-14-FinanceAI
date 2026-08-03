package com.finance_ai_backend.api.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.finance_ai_backend.api.domain.models.Usuario;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Tag(name = "Usuário", description = "Endpoints relacionados ao usuário autenticado")
@RestController
@RequestMapping("usuario")
@SecurityRequirement(name = "bearerAuth") 
public class UsuarioController {

    @Operation(
            summary = "Retorna dados do usuário autenticado",
            description = "Confirma que o usuário está autenticado e retorna uma mensagem simples com seu nome de usuário"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário autenticado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    })
    @GetMapping()
    public String home(@AuthenticationPrincipal Usuario usuario){
        return "cadastrado: " + usuario.getUsername();
    }
    
}