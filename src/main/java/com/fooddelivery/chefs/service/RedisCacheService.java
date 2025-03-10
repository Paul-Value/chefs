package com.fooddelivery.chefs.service;

import com.fooddelivery.chefs.model.dto.GeocodingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisCacheService {
    private final RedisTemplate<String, Object> redisTemplate;

    public void cacheAddress(String query, List<GeocodingResponse> addresses) {
        redisTemplate.opsForValue().set(query, addresses, 24, TimeUnit.HOURS);
    }

    @Nullable
    public List<GeocodingResponse> getCachedAddress(String query) {
        Object cachedData = redisTemplate.opsForValue().get(query);
        if (cachedData instanceof List<?> list) {
            // Проверка типа элементов списка
            if (list.stream().allMatch(GeocodingResponse.class::isInstance)) {
                return (List<GeocodingResponse>) list;
            }
        }
        return Collections.emptyList();
    }
}
