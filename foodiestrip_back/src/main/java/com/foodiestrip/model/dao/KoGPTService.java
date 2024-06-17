package com.foodiestrip.model.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class KoGPTService {

    @Value("${api.url}")
    private String apiUrl;

    @Value("${rest.api.key}")
    private String restApiKey;

    @Data
    public static class RequestBody {
        private String prompt;
        private int max_tokens;
        private double temperature;
        private double top_p;
        private int n = 1;
    }

    @Data
    public static class ResponseBody {
        private String id;
        private Generation[] generations;
        private Usage usage;
    }

    @Data
    public static class Generation {
        private String text;
        private int tokens;
    }

    @Data
    public static class Usage {
        private int prompt_tokens;
        private int generated_tokens;
        private int total_tokens;
    }

    public ResponseBody generateText(String prompt) {
        RequestBody requestBody = new RequestBody();
        requestBody.setPrompt(prompt);
        requestBody.setMax_tokens(50);
        requestBody.setTemperature(0.3);
        requestBody.setTop_p(0.7);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "KakaoAK " + restApiKey);

        HttpEntity<RequestBody> requestEntity = new HttpEntity<>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(apiUrl, requestEntity, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            try {
                return objectMapper.readValue(responseEntity.getBody(), ResponseBody.class);
            } catch (Exception e) {
                throw new RuntimeException("Error occurred while parsing response body", e);
            }
        } else {
            throw new RuntimeException("Error from API: " + responseEntity.getStatusCodeValue());
        }
    }
}

