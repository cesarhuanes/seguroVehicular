package com.segurovehiculo.service;

import com.segurovehiculo.domain.SimulationResult;
import com.segurovehiculo.domain.VehicleInfo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface InsuranceService {
    public Mono<SimulationResult> simulatePremium(VehicleInfo vehicle);
    public Mono<VehicleInfo> saveVehicle(VehicleInfo v);
    public Flux<VehicleInfo> getVehicles();
    public Flux<SimulationResult> getSimulations();
    public Mono<Void> deleteVehicle(Long id);
    public Mono<VehicleInfo> updateVehicle(Long id, VehicleInfo v);
}
