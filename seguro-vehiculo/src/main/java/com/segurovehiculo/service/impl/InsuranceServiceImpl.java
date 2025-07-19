package com.segurovehiculo.service.impl;

import com.segurovehiculo.domain.SimulationResult;
import com.segurovehiculo.domain.VehicleInfo;
import com.segurovehiculo.model.SimulationResultRepository;
import com.segurovehiculo.model.VehicleInfoRepository;
import com.segurovehiculo.service.InsuranceService;
import com.segurovehiculo.service.PremiumCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InsuranceServiceImpl implements InsuranceService {

    private final VehicleInfoRepository vehicleRepo;
    private final SimulationResultRepository resultRepo;
    private final PremiumCacheService cacheService;

    private final BigDecimal BASE = BigDecimal.valueOf(500);

    @Override
    public Mono<SimulationResult> simulatePremium(VehicleInfo vehicle) {
        return cacheService.getCachedResult(vehicle)
                .switchIfEmpty(
                        calculate(vehicle)
                                .flatMap(result -> resultRepo.save(result)
                                        .doOnNext(saved -> cacheService.cacheResult(vehicle, saved))
                                )
                );
    }

    private Mono<SimulationResult> calculate(VehicleInfo vehicle) {
        BigDecimal adjustment = BigDecimal.ZERO;

        if (vehicle.getYear() > 2015)
            adjustment = adjustment.add(BASE.multiply(BigDecimal.valueOf(0.15)));

        if ("carga".equalsIgnoreCase(vehicle.getUsage()))
            adjustment = adjustment.add(BASE.multiply(BigDecimal.valueOf(0.10)));

        if (vehicle.getDriverAge() > 50)
            adjustment = adjustment.subtract(BASE.multiply(BigDecimal.valueOf(0.05)));

        if ("BMW".equalsIgnoreCase(vehicle.getBrand()))
            adjustment = adjustment.add(BASE.multiply(BigDecimal.valueOf(0.20)));
        else if ("Audi".equalsIgnoreCase(vehicle.getBrand()))
            adjustment = adjustment.add(BASE.multiply(BigDecimal.valueOf(0.10)));

        BigDecimal total = BASE.add(adjustment);
        SimulationResult result = new SimulationResult(null, vehicle.getId(), BASE, adjustment, total, LocalDateTime.now());

        return Mono.just(result);
    }

    @Override
    public Mono<VehicleInfo> saveVehicle(VehicleInfo v) {
        v.setCreatedAt(LocalDateTime.now());
        return vehicleRepo.save(v);
    }

    @Override
    public Flux<VehicleInfo> getVehicles() {
        return vehicleRepo.findAll();
    }

    @Override
    public Flux<SimulationResult> getSimulations() {
        return resultRepo.findAll();
    }

    @Override
    public Mono<Void> deleteVehicle(Long id) {
        return resultRepo.findByVehicleId(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehículo con ID " + id + " no existe")))
                .flatMap(sim -> resultRepo.deleteById(sim.getId()))
                .then(vehicleRepo.deleteById(id));
    }

    @Override
    public Mono<VehicleInfo> updateVehicle(Long id, VehicleInfo v) {
        return vehicleRepo.findById(id)
                .flatMap(existing -> {
                    existing.setBrand(v.getBrand());
                    existing.setModel(v.getModel());
                    existing.setYear(v.getYear());
                    existing.setUsage(v.getUsage());
                    existing.setDriverAge(v.getDriverAge());
                    return vehicleRepo.save(existing);
                });
    }
}
