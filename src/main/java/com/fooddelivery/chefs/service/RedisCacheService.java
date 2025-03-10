package com.fooddelivery.chefs.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisCacheService {
    private final RedisTemplate<String, Object> redisTemplate;

    public void cacheAddress(String query, List<AddressResponse> addresses) {
        redisTemplate.opsForValue().set(query, addresses, 24, TimeUnit.HOURS);
    }

    public List<AddressResponse> getCachedAddress(String query) {
        return (List<AddressResponse>) redisTemplate.opsForValue().get(query);
    }
}
