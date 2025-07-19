package com.segurovehiculo.model;

import com.segurovehiculo.domain.SimulationResult;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
@Repository
public interface SimulationResultRepository extends ReactiveCrudRepository<SimulationResult, Long> {
    // Buscar simulaciones por vehículo
    Flux<SimulationResult> findByVehicleId(Long vehicleId);
}
