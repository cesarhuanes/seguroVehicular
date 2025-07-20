package com.segurovehiculo.service.impl;

import com.segurovehiculo.domain.SimulationResult;
import com.segurovehiculo.domain.VehicleInfo;
import com.segurovehiculo.model.SimulationResultRepository;
import com.segurovehiculo.model.VehicleInfoRepository;
import com.segurovehiculo.service.InsuranceService;
import com.segurovehiculo.service.PremiumCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class InsuranceServiceImpl implements InsuranceService {



    private final VehicleInfoRepository vehicleRepo;
    private final SimulationResultRepository resultRepo;
    private final PremiumCacheService cacheService;

    private final BigDecimal BASE = BigDecimal.valueOf(500);

    @Override
    public Mono<SimulationResult> simulatePremium(VehicleInfo vehicle) {
        log.info("Simulando prima para vehículo: {}", vehicle);
        return cacheService.getCachedResult(vehicle)
                .doOnNext(result -> log.info("Resultado obtenido desde caché: {}", result))
                .switchIfEmpty(
                        calculate(vehicle)
                                .flatMap(result -> resultRepo.save(result)
                                        .doOnNext(saved -> cacheService.cacheResult(vehicle, saved))
                                )
                );
    }

    private Mono<SimulationResult> calculate(VehicleInfo vehicle) {
        log.debug("Calculando prima para vehículo ID {}", vehicle.getId());
        BigDecimal adjustment = BigDecimal.ZERO;

        if (vehicle.getYear() > 2015) {
            adjustment = adjustment.add(BASE.multiply(BigDecimal.valueOf(0.15)));
            log.debug("Ajuste aplicado por año > 2015: +15%");
        }

        if ("carga".equalsIgnoreCase(vehicle.getUsage())) {
            adjustment = adjustment.add(BASE.multiply(BigDecimal.valueOf(0.10)));
            log.debug("Ajuste aplicado por uso 'carga': +10%");
        }

        if (vehicle.getDriverAge() > 50) {
            adjustment = adjustment.subtract(BASE.multiply(BigDecimal.valueOf(0.05)));
            log.debug("Ajuste aplicado por edad > 50: -5%");
        }

        if ("BMW".equalsIgnoreCase(vehicle.getBrand())) {
            adjustment = adjustment.add(BASE.multiply(BigDecimal.valueOf(0.20)));
            log.debug("Ajuste aplicado por marca BMW: +20%");
        } else if ("Audi".equalsIgnoreCase(vehicle.getBrand())) {
            adjustment = adjustment.add(BASE.multiply(BigDecimal.valueOf(0.10)));
            log.debug("Ajuste aplicado por marca Audi: +10%");
        }

        BigDecimal total = BASE.add(adjustment);
        log.info("Prima final calculada: {}, ajuste total: {}", total, adjustment);
        SimulationResult result = new SimulationResult(null, vehicle.getId(), BASE, adjustment, total, LocalDateTime.now());

        return Mono.just(result);
    }

    @Override
    public Mono<VehicleInfo> saveVehicle(VehicleInfo v) {
        log.info("Guardando vehículo nuevo: {}", v);
        v.setCreatedAt(LocalDateTime.now());
        return vehicleRepo.save(v)
                .doOnSuccess(saved -> log.info("Vehículo guardado exitosamente: {}", saved));
    }

    @Override
    public Flux<VehicleInfo> getVehicles() {
        log.info("Solicitando listado de vehículos desde base de datos");
        return vehicleRepo.findAll()
                .doOnNext(vehicle -> log.debug("Vehículo recuperado: {}", vehicle));
    }

    @Override
    public Flux<SimulationResult> getSimulations() {
        log.info("Solicitando listado de simulaciones desde base de datos");
        return resultRepo.findAll()
                .doOnNext(simulation -> log.debug("Simulación recuperada: {}", simulation));

    }

    @Override
    public Mono<Void> deleteVehicle(Long id) {
        log.info("Intentando eliminar vehículo con ID {}", id);
        return resultRepo.findByVehicleId(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehículo con ID " + id + " no existe")))
                .flatMap(sim -> resultRepo.deleteById(sim.getId()))
                .then(vehicleRepo.deleteById(id))
                .doOnSuccess(v -> log.info("Vehículo con ID {} eliminado", id));
    }

    @Override
    public Mono<VehicleInfo> updateVehicle(Long id, VehicleInfo v) {
        log.info("Actualizando vehículo con ID {} con nuevos datos: {}", id, v);
        return vehicleRepo.findById(id)
                .flatMap(existing -> {
                    existing.setBrand(v.getBrand());
                    existing.setModel(v.getModel());
                    existing.setYear(v.getYear());
                    existing.setUsage(v.getUsage());
                    existing.setDriverAge(v.getDriverAge());
                    return vehicleRepo.save(existing);
                }).doOnSuccess(updated -> log.info("Vehículo actualizado: {}", updated));

    }
}
