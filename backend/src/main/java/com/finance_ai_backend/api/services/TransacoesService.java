package com.finance_ai_backend.api.services;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import com.finance_ai_backend.api.domain.dtos.RetornoClassificacaoDTO;

@Service
public class TransacoesService {

    private final RestClient restClient;
    private final String predictApiKeyHeaderName;
    private final String apiKey;

    public TransacoesService(
        RestClient.Builder builder,
        @Value("${predict_api.host}") String predictApiHost,
        @Value("${predict_api_key.header_name}") String predictApiKeyHeaderName,
        @Value("${predict_api_key}") String apiKey
    ) {
        this.predictApiKeyHeaderName = predictApiKeyHeaderName;
        this.apiKey = apiKey;
        this.restClient = builder
            .baseUrl(predictApiHost + "/predict")
            .requestFactory(new SimpleClientHttpRequestFactory())
            .build();
    }

    public RetornoClassificacaoDTO classificar(String descricao) {
        Map<String, String> body = Map.of("descricao", descricao);

        return restClient.post()
            .uri("/transacoes")
            .header(this.predictApiKeyHeaderName, this.apiKey)
            .contentType(MediaType.APPLICATION_JSON)
            .body(body)
            .retrieve()
            .body(RetornoClassificacaoDTO.class);
    }
}