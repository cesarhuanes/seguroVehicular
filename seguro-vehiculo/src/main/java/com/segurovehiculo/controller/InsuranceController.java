package com.segurovehiculo.controller;

import com.segurovehiculo.domain.SimulationResult;
import com.segurovehiculo.domain.VehicleInfo;
import com.segurovehiculo.service.InsuranceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/insurance")
@RequiredArgsConstructor
public class InsuranceController {
    private final InsuranceService insuranceService;

    // 🚗 Guardar nuevo vehículo
    @PostMapping("/vehicle")
    public Mono<VehicleInfo> createVehicle(@RequestBody VehicleInfo vehicleInfo) {
        return insuranceService.saveVehicle(vehicleInfo);
    }

    // 🎯 Simular prima para un vehículo
    @PostMapping("/simulate")
    public Mono<SimulationResult> simulate(@RequestBody VehicleInfo vehicleInfo) {
        return insuranceService.simulatePremium(vehicleInfo);
    }

    // 📋 Listar vehículos registrados
    @GetMapping("/vehicles")
    public Mono<ResponseEntity<List<VehicleInfo>>> getVehicles() {
        return insuranceService.getVehicles()
                .collectList()
                .map(ResponseEntity::ok);
    }

    // 📊 Listar todas las simulaciones realizadas
    @GetMapping("/simulations")
    public Flux<SimulationResult> getSimulations() {
        return insuranceService.getSimulations();

    }

    @PutMapping("/{id}")
    public Mono<VehicleInfo> update(@PathVariable Long id, @RequestBody VehicleInfo v) {
        return insuranceService.updateVehicle(id, v);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable Long id) {
        return insuranceService.deleteVehicle(id);
    }
}
