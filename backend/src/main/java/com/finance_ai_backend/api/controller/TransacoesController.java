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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Transações", description = "Envio de transações financeiras e geração/consulta do perfil do usuário")
@RestController
@RequestMapping("api/v1/")
@SecurityRequirement(name = "bearerAuth") 
public class TransacoesController {
    private final TransacoesService transacoesService;

    public TransacoesController(TransacoesService transacoesService) {
        this.transacoesService = transacoesService;
    }

    @Operation(
            summary = "Envia um lote de transações",
            description = "Recebe uma lista de transações financeiras do usuário autenticado e as salva para posterior análise"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Transações salvas com sucesso"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Uma ou mais transações inválidas",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"descricao\": \"Esse item é necessário\"}")
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    })
    @PostMapping("transacoes")
    public ResponseEntity<Void> gerarTrasacoesEmLote(
        @Valid @RequestBody List<TransacaoInputDTO> transacaoInputDTOs,
        @AuthenticationPrincipal Usuario usuario 
    ) {
        transacoesService.salvarTransacoes(
            usuario,
            transacaoInputDTOs
        );
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Classifica uma transação (consulta sem salvar)",
            description = "Classifica a categoria de uma única transação sem persistir"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Classificação retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Transação inválida"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    })
    @PostMapping("transacao")
    public ResponseEntity<Void> gerarTransacao(
        @Valid @RequestBody TransacaoInputDTO transacaoInputDTO
    ) {
        transacoesService.salvarTransacao(transacaoInputDTO.getDescricao());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Executa a análise financeira do usuário",
            description = "Processa as transações salvas do usuário autenticado e gera seu perfil financeiro com base em modelo preditivo"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Análise executada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    })
    @PostMapping("analisar")
    public ResponseEntity<Void> gerarAnaliseDePerfil(
        @AuthenticationPrincipal Usuario usuario 
    ) {
        transacoesService.executarAnaliseFinanceira(usuario);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Busca o perfil financeiro do usuário",
            description = "Retorna o perfil financeiro categorizado e as sugestões geradas para o usuário autenticado"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil encontrado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Perfil ainda não foi gerado para o usuário",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"erro\": \"Perfil não encontrado para o usuário\"}")
                    )
            )
    })
    @GetMapping("perfil")
    public ResponseEntity<PerfilUsuarioRetornoDTO> buscarPerfil(
        @AuthenticationPrincipal Usuario usuario
    ) {
        return ResponseEntity.ok(transacoesService.buscarPerfil(usuario));
    }
    

}