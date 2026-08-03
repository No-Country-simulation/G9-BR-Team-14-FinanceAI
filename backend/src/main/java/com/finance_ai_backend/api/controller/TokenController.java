package com.finance_ai_backend.api.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.finance_ai_backend.api.domain.dtos.TokenGeracaoDTO;
import com.finance_ai_backend.api.domain.dtos.TokenRespostaDTO;
import com.finance_ai_backend.api.domain.exceptions.TokenInvalidoException;
import com.finance_ai_backend.api.services.TokenService;

import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;

@Tag(name = "Token", description = "Geração e revogação de tokens de autenticação (login e logout)")
@RestController
@RequestMapping("token")
@SecurityRequirements
public class TokenController {

    private static final String PREFIXO_BEARER = "Bearer ";

    private final TokenService tokenService;

    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Token gerado com sucesso"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Login ou senha não informados",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"login\": \"O campo login é obrigatório\"}")
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Credenciais inválidas",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"erro\": \"Credenciais inválidas\"}")
                    )
            )
    })
    @Operation(
            summary = "Gera um token de autenticação",
            description = "Autentica o usuário com login e senha, retornando um token JWT para uso nas demais rotas da API"
    )
    @PostMapping()
    public TokenRespostaDTO gerarToken(@RequestBody @Valid TokenGeracaoDTO dto) {
        return tokenService.gerarToken(dto);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Logout realizado com sucesso"),
            @ApiResponse(
                    responseCode = "401",
                    description = "Header Authorization ausente ou inválido",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"erro\": \"Header Authorization ausente ou inválido\"}")
                    )
            )
    })
    @Operation(
            summary = "Revoga um token (logout)",
            description = "Invalida o token informado no header Authorization, adicionando-o à blacklist para impedir seu uso futuro"
    )
    @PostMapping("blacklist")
    public String blacklistToken(@RequestHeader("Authorization") String cabecalhoAutorizacao) {
        if (cabecalhoAutorizacao == null || !cabecalhoAutorizacao.startsWith(PREFIXO_BEARER)) {
            throw new TokenInvalidoException("Header Authorization ausente ou inválido");
        }

        String token = cabecalhoAutorizacao.substring(PREFIXO_BEARER.length());
        tokenService.revogarToken(token);
        return "logout realizado";
    }
}
