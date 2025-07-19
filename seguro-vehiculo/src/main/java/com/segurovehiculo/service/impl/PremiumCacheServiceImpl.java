package com.segurovehiculo.service.impl;

import com.segurovehiculo.domain.SimulationResult;
import com.segurovehiculo.domain.VehicleInfo;
import com.segurovehiculo.service.PremiumCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class PremiumCacheServiceImpl implements PremiumCacheService {
    private final ReactiveRedisTemplate<String, SimulationResult> redisTemplate;

    private String generateKey(VehicleInfo v) {
        return "premium:" + v.getBrand() + ":" + v.getModel() + ":" + v.getYear()
                + ":" + v.getUsage() + ":" + v.getDriverAge();
    }

    public Mono<SimulationResult> getCachedResult(VehicleInfo v) {
        return redisTemplate.opsForValue().get(generateKey(v));
    }

    public Mono<Void> cacheResult(VehicleInfo v, SimulationResult result) {
        return redisTemplate.opsForValue()
                .set(generateKey(v), result, Duration.ofMinutes(5))
                .then();
    }
}
