package com.segurovehiculo.controller;

import com.segurovehiculo.domain.SimulationResult;
import com.segurovehiculo.domain.VehicleInfo;
import com.segurovehiculo.service.InsuranceService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/insurance")
@RequiredArgsConstructor
public class InsuranceController {
    private static final Logger logger = LoggerFactory.getLogger(InsuranceController.class);
    private final InsuranceService insuranceService;

    // Guardar nuevo vehículo
    @PostMapping("/vehicle")
    public Mono<VehicleInfo> createVehicle(@RequestBody VehicleInfo vehicleInfo) {
        logger.info("Creando nuevo vehículo: {}", vehicleInfo);
        return insuranceService.saveVehicle(vehicleInfo);
    }

    // Simular prima para un vehículo
    @PostMapping("/simulate")
    public Mono<SimulationResult> simulate(@RequestBody VehicleInfo vehicleInfo) {
        logger.info("Simulando prima para vehículo: {}", vehicleInfo);
        return insuranceService.simulatePremium(vehicleInfo);
    }

    //  Listar vehículos registrados
    @GetMapping("/vehicles")
    public Mono<ResponseEntity<List<VehicleInfo>>> getVehicles() {
        logger.info("Solicitando lista de vehículos registrados");
        return insuranceService.getVehicles()
                .collectList()
                .map(ResponseEntity::ok);
    }

    // Listar todas las simulaciones realizadas
    @GetMapping("/simulations")
    public Flux<SimulationResult> getSimulations() {
        logger.info("Solicitando lista de simulaciones realizadas");
        return insuranceService.getSimulations();

    }
    //actualiza vehiculo
    @PutMapping("/{id}")
    public Mono<VehicleInfo> update(@PathVariable Long id, @RequestBody VehicleInfo v) {
        logger.info("Actualizando vehículo con ID {}: {}", id, v);
        return insuranceService.updateVehicle(id, v);
    }

    //elimina vehiculo
    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable Long id) {
        logger.info("Eliminando vehículo con ID: {}", id);
        return insuranceService.deleteVehicle(id);
    }
}
