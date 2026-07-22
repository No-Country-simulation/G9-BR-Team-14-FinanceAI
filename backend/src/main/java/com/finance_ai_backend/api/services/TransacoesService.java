package com.finance_ai_backend.api.services;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.finance_ai_backend.api.domain.dtos.RetornoClassificacaoDTO;
import com.finance_ai_backend.api.domain.dtos.TransacaoInputDTO;
import com.finance_ai_backend.api.domain.models.CategoriaEnum;
import com.finance_ai_backend.api.domain.models.Transacao;
import com.finance_ai_backend.api.domain.models.Usuario;
import com.finance_ai_backend.api.domain.repositories.TransacaoRepositorio;

@Service
public class TransacoesService {

    private final RestClient restClient;
    private final String predictApiKeyHeaderName;
    private final String apiKey;

    private final TransacaoRepositorio transacaoRepositorio;

    public TransacoesService(
        RestClient.Builder builder,
        TransacaoRepositorio transacaoRepositorio,
        @Value("${predict_api.host}") String predictApiHost,
        @Value("${predict_api_key.header_name}") String predictApiKeyHeaderName,
        @Value("${predict_api_key}") String apiKey
    ) {
        this.transacaoRepositorio = transacaoRepositorio;

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
}