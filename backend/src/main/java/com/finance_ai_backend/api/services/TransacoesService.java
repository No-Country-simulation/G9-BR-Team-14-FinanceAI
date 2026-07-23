package com.finance_ai_backend.api.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.finance_ai_backend.api.domain.dtos.AnaliseFinanceiraDTO;
import com.finance_ai_backend.api.domain.dtos.AnalisePerfilRetornoDTO;
import com.finance_ai_backend.api.domain.dtos.CategoriaTotalDTO;
import com.finance_ai_backend.api.domain.dtos.PerfilUsuarioRetornoDTO;
import com.finance_ai_backend.api.domain.dtos.RetornoClassificacaoDTO;
import com.finance_ai_backend.api.domain.dtos.SugestoesRetornoDTO;
import com.finance_ai_backend.api.domain.dtos.TransacaoInputDTO;
import com.finance_ai_backend.api.domain.exceptions.PerfilNaoEncontradoException;
import com.finance_ai_backend.api.domain.models.CategoriaEnum;
import com.finance_ai_backend.api.domain.models.PerfilUsuario;
import com.finance_ai_backend.api.domain.models.Transacao;
import com.finance_ai_backend.api.domain.models.Usuario;
import com.finance_ai_backend.api.domain.repositories.PerfilUsuarioRepositorio;
import com.finance_ai_backend.api.domain.repositories.TransacaoRepositorio;

@Service
public class TransacoesService {

    private final RestClient restClient;
    private final String predictApiKeyHeaderName;
    private final String apiKey;

    private final TransacaoRepositorio transacaoRepositorio;
    private final PerfilUsuarioRepositorio perfilUsuarioRepositorio;

    public TransacoesService(
        RestClient.Builder builder,
        TransacaoRepositorio transacaoRepositorio,
        PerfilUsuarioRepositorio perfilUsuarioRepositorio,
        @Value("${predict_api.host}") String predictApiHost,
        @Value("${predict_api_key.header_name}") String predictApiKeyHeaderName,
        @Value("${predict_api_key}") String apiKey
    ) {
        this.transacaoRepositorio = transacaoRepositorio;
        this.perfilUsuarioRepositorio = perfilUsuarioRepositorio;

        this.predictApiKeyHeaderName = predictApiKeyHeaderName;
        this.apiKey = apiKey;
        this.restClient = builder
            .baseUrl(predictApiHost + "/predict")
            .requestFactory(new SimpleClientHttpRequestFactory())
            .build();
    }
    public void salvarTransacoes(
        Usuario usuario, 
        List<TransacaoInputDTO> transacaoInputDTOs
    ){
        

        List<Map<String, String>> listaDescricoes = transacaoInputDTOs
        .stream()
        .map(TransacaoInputDTO::getDescricao)
        .distinct()
        .map(descricao -> Map.of("descricao", descricao))
        .collect(Collectors.toList());
        
        Map<String, String> mapaDescricaoCategoria = restClient.post()
        .uri("/lote_transacoes")
        .header(this.predictApiKeyHeaderName, this.apiKey)
        .body(listaDescricoes)
        .retrieve()
        .body(new ParameterizedTypeReference<List<RetornoClassificacaoDTO>>() {})
        .stream()
        .collect(Collectors.toMap(
            RetornoClassificacaoDTO::descricao,
            RetornoClassificacaoDTO::categoria,
            (categoriaAntiga, categoriaNova) -> categoriaNova
        ));
        

        List<Transacao> listaTransacoes = transacaoInputDTOs
        .stream()
        .map(item -> {
            return Transacao
                .builder()
                .usuario(usuario)
                .descricao(item.getDescricao())
                .categoria(
                    CategoriaEnum.valueOf(mapaDescricaoCategoria.get(item.getDescricao()))
                )
                .valor(item.getValor())
                .data(item.getData())
                .build();
        })
        .collect(Collectors.toList());
        
        transacaoRepositorio.saveAll(listaTransacoes);
    }

    public void executarAnaliseFinanceira(Usuario usuario){
        List<CategoriaTotalDTO> totalPorCategoriaLista = transacaoRepositorio.somarPorCategoria(usuario.getPk());

        Map<CategoriaEnum, BigDecimal> totalPorCategoria  = totalPorCategoriaLista
        .stream()
        .collect(Collectors.toMap(
            CategoriaTotalDTO::categoria,
            CategoriaTotalDTO::total
        ));

        AnaliseFinanceiraDTO analiseFinanceiraDTO =  AnaliseFinanceiraDTO.builder()
        .rendaMensal(totalPorCategoria.getOrDefault(CategoriaEnum.GANHOS, BigDecimal.ZERO))
        .valorInvestido(totalPorCategoria.getOrDefault(CategoriaEnum.POUPANCA, BigDecimal.ZERO))
        .gastoAlimentacao(totalPorCategoria.getOrDefault(CategoriaEnum.ALIMENTACAO, BigDecimal.ZERO))
        .gastoTransporte(totalPorCategoria.getOrDefault(CategoriaEnum.TRANSPORTE, BigDecimal.ZERO))
        .gastoSaude(totalPorCategoria.getOrDefault(CategoriaEnum.SAUDE, BigDecimal.ZERO))
        .gastoMoradia(totalPorCategoria.getOrDefault(CategoriaEnum.MORADIA, BigDecimal.ZERO))
        .gastoEducacao(totalPorCategoria.getOrDefault(CategoriaEnum.EDUCACAO, BigDecimal.ZERO))
        .gastoLazer(totalPorCategoria.getOrDefault(CategoriaEnum.LAZER, BigDecimal.ZERO))
        .gastoServicos(totalPorCategoria.getOrDefault(CategoriaEnum.SERVICOS, BigDecimal.ZERO))
        .gastoAssinaturas(totalPorCategoria.getOrDefault(CategoriaEnum.ASSINATURAS, BigDecimal.ZERO))
        .gastoDividas(totalPorCategoria.getOrDefault(CategoriaEnum.DIVIDAS, BigDecimal.ZERO))
        .gastoOutras(totalPorCategoria.getOrDefault(CategoriaEnum.OUTRAS, BigDecimal.ZERO))
        .build();

        AnalisePerfilRetornoDTO analisePerfilRetornoDTO = restClient.post()
        .uri("/perfil")
        .header(this.predictApiKeyHeaderName, this.apiKey)
        .body(analiseFinanceiraDTO)
        .retrieve()
        .body(AnalisePerfilRetornoDTO.class);

        SugestoesRetornoDTO sugestoes = restClient.post()
        .uri("/sugestoes")
        .header(this.predictApiKeyHeaderName, this.apiKey)
        .body(analiseFinanceiraDTO)
        .retrieve()
        .body(SugestoesRetornoDTO.class);

        System.out.println(sugestoes.sugestoesAtivas());

        perfilUsuarioRepositorio.save(
            PerfilUsuario.builder()
            .pk(usuario.getPk())
            .sugestoesPerfilUsuario(sugestoes.sugestoesAtivas())
            .perfilCategorizado(analisePerfilRetornoDTO.perfil())
            .build()
        );
    }

    public PerfilUsuarioRetornoDTO buscarPerfil(Usuario usuario) {
        PerfilUsuario perfilUsuario = perfilUsuarioRepositorio.findById(usuario.getPk())
            .orElseThrow(() -> new PerfilNaoEncontradoException("Perfil não encontrado para o usuário"));

        return new PerfilUsuarioRetornoDTO(
            perfilUsuario.getPerfilCategorizado(),
            perfilUsuario.getSugestoesPerfilUsuario()
        );
    }
}