package com.seguro.vehiculo.seguro_vehiculo.service;

import com.segurovehiculo.domain.SimulationResult;
import com.segurovehiculo.domain.VehicleInfo;
import com.segurovehiculo.service.impl.PremiumCacheServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PremiumCacheServiceImplTest {

    @Mock
    private ReactiveRedisTemplate<String, SimulationResult> redisTemplate;

    @Mock
    private ReactiveValueOperations<String, SimulationResult> valueOps;

    @InjectMocks
    private PremiumCacheServiceImpl cacheService;

    @Test
    void getCachedResult_shouldReturnCachedSimulation() {
        VehicleInfo vehicle = new VehicleInfo();
        vehicle.setBrand("Toyota");
        vehicle.setModel("Corolla");
        vehicle.setYear(2020);
        vehicle.setUsage("personal");
        vehicle.setDriverAge(40);

        SimulationResult result = new SimulationResult(101L, 1L, new BigDecimal("500"), BigDecimal.ZERO, new BigDecimal("500"), LocalDateTime.now());

        String key = "premium:Toyota:Corolla:2020:personal:40";

        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.get(key)).thenReturn(Mono.just(result));

        StepVerifier.create(cacheService.getCachedResult(vehicle))
                .expectNext(result)
                .verifyComplete();

        verify(redisTemplate).opsForValue();
        verify(valueOps).get(key);
    }

    @Test
    void cacheResult_shouldStoreSimulationResultInRedis() {
        VehicleInfo vehicle = new VehicleInfo();
        vehicle.setBrand("BMW");
        vehicle.setModel("X5");
        vehicle.setYear(2022);
        vehicle.setUsage("carga");
        vehicle.setDriverAge(55);

        SimulationResult result = new SimulationResult(202L, 2L, new BigDecimal("500"), new BigDecimal("400"), new BigDecimal("900"), LocalDateTime.now());

        String key = "premium:BMW:X5:2022:carga:55";

        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.set(key, result, Duration.ofMinutes(5))).thenReturn(Mono.just(true));

        StepVerifier.create(cacheService.cacheResult(vehicle, result))
                .verifyComplete();

        verify(redisTemplate).opsForValue();
        verify(valueOps).set(key, result, Duration.ofMinutes(5));
    }
}
