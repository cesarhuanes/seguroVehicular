package com.segurovehiculo.service;

import com.segurovehiculo.domain.SimulationResult;
import com.segurovehiculo.domain.VehicleInfo;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
@Repository
public interface PremiumCacheService {
    public Mono<SimulationResult> getCachedResult(VehicleInfo v);
    public Mono<Void> cacheResult(VehicleInfo v, SimulationResult result);
}
