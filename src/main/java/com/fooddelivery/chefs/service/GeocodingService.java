package com.fooddelivery.chefs.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fooddelivery.chefs.model.dto.GeocodingResponse;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class GeocodingService {
    private final RestTemplate restTemplate;
    private final RedisCacheService redisCacheService;

    @Autowired
    public GeocodingService(RedisCacheService redisCacheService) {
        this.restTemplate = new RestTemplate();
        this.restTemplate.setInterceptors(Collections.singletonList(new UserAgentInterceptor()));
        this.redisCacheService = redisCacheService;
    }

    public List<GeocodingResponse> searchAddress(String query) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Проверка кэша
        List<GeocodingResponse> cached = redisCacheService.getCachedAddress(query);
        if (cached != null && !cached.isEmpty()) return cached;

        // Формирование URL с кодированием параметров
        String url = UriComponentsBuilder.fromUriString("https://nominatim.openstreetmap.org/search")
                .queryParam("q", query)
                .queryParam("format", "json")
                .queryParam("addressdetails", "1")
                .build() // Собирает компоненты URI с автоматическим кодированием
                .toUriString();

        // Запрос к Nominatim
        ResponseEntity<OpenStreetMapResponse[]> response = restTemplate.getForEntity(
                url,
                OpenStreetMapResponse[].class
        );

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            List<GeocodingResponse> results = Arrays.stream(response.getBody())
                    .map(item -> new GeocodingResponse(
                            item.getDisplayName(),
                            new BigDecimal(item.getLat()),
                            new BigDecimal(item.getLon()),
                            item.getOsmId()
                    ))
                    .toList();

            // Сохранение в кэш
            redisCacheService.cacheAddress(query, results);
            return results;
        }

        return Collections.emptyList();
    }

    // DTO для парсинга ответа Nominatim
    @Data
    private static class OpenStreetMapResponse {
        @JsonProperty("display_name")
        private String displayName;
        private String lat;
        private String lon;
        @JsonProperty("osm_id")
        private String osmId;
    }

    // Интерцептор с аннотациями @NonNull
    private static class UserAgentInterceptor implements ClientHttpRequestInterceptor {
        @Override
        public ClientHttpResponse intercept(
                @NonNull org.springframework.http.HttpRequest request,
                byte @NonNull [] body,
                @NonNull ClientHttpRequestExecution execution
        ) throws IOException {
            request.getHeaders().set("User-Agent", "FoodDeliveryApp/1.0 (contact@example.com)");
            return execution.execute(request, body);
        }
    }
}