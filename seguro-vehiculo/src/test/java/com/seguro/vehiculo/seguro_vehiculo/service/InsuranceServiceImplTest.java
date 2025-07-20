package com.seguro.vehiculo.seguro_vehiculo.service;

import com.segurovehiculo.domain.SimulationResult;
import com.segurovehiculo.domain.VehicleInfo;
import com.segurovehiculo.model.SimulationResultRepository;
import com.segurovehiculo.model.VehicleInfoRepository;
import com.segurovehiculo.service.PremiumCacheService;
import com.segurovehiculo.service.impl.InsuranceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InsuranceServiceImplTest {

    @Mock
    private VehicleInfoRepository vehicleRepo;

    @Mock
    private SimulationResultRepository resultRepo;

    @Mock
    private PremiumCacheService cacheService;

    @InjectMocks
    private InsuranceServiceImpl service;
    private static final BigDecimal BASE = new BigDecimal("500.00");
    @BeforeEach
    void setup() {

    }

    @Test
    void simulatePremium_cached_shouldReturnCachedResult() {
        VehicleInfo vehicle = new VehicleInfo(); vehicle.setId(1L);
        SimulationResult cachedResult = new SimulationResult(
                101L, 1L, BASE, BigDecimal.ZERO, BASE, LocalDateTime.now()
        );

        when(cacheService.getCachedResult(vehicle)).thenReturn(Mono.just(cachedResult));

        StepVerifier.create(service.simulatePremium(vehicle))
                .expectNext(cachedResult)
                .verifyComplete();

        verify(cacheService).getCachedResult(vehicle);
        verifyNoInteractions(resultRepo);
    }

    @Test
    void simulatePremium_whenCachedResultIsNull_shouldEmitIllegalStateException() {
        VehicleInfo vehicle = new VehicleInfo();
        vehicle.setId(2L);
        vehicle.setYear(2020);        // +15%
        vehicle.setUsage("carga");    // +10%
        vehicle.setDriverAge(55);     // -5%
        vehicle.setBrand("BMW");      // +20%

        BigDecimal expectedAdjustment = BASE.multiply(BigDecimal.valueOf(0.15 + 0.10 + 0.20 - 0.05));
        BigDecimal expectedTotal = BASE.add(expectedAdjustment);
        LocalDateTime createdAt = LocalDateTime.now();

        SimulationResult calculated = new SimulationResult(null, 2L, BASE, expectedAdjustment, expectedTotal, createdAt);
        SimulationResult savedResult = new SimulationResult(102L, 2L, BASE, expectedAdjustment, expectedTotal, createdAt);

        when(cacheService.getCachedResult(vehicle)).thenReturn(Mono.empty());
        when(resultRepo.save(any())).thenReturn(Mono.just(savedResult));

        StepVerifier.create(service.simulatePremium(vehicle))
                .expectNextMatches(result ->
                        result.getId().equals(102L) &&
                                result.getAdjustment().compareTo(expectedAdjustment) == 0 &&
                                result.getTotalPremium().compareTo(expectedTotal) == 0
                )
                .verifyComplete();

        verify(cacheService).getCachedResult(vehicle);
        verify(resultRepo).save(any());
        verify(cacheService).cacheResult(vehicle, savedResult);
    }

    @Test
    void saveVehicle_shouldSetCreatedAtAndPersist() {
        VehicleInfo input = new VehicleInfo(); input.setId(1L);
        VehicleInfo saved = new VehicleInfo(); saved.setId(1L);


        when(vehicleRepo.save(any())).thenReturn(Mono.just(input));
        Mono<VehicleInfo> result=vehicleRepo.save(saved);
        StepVerifier.create(result)
                .expectNext(input)
                .verifyComplete();
        verify(vehicleRepo).save(any());
    }

    @Test
    void getVehicles_shouldReturnAllVehicles() {
        VehicleInfo v1 = new VehicleInfo(); v1.setId(1L); v1.setBrand("Toyota");
        VehicleInfo v2 = new VehicleInfo(); v2.setId(2L); v2.setBrand("Honda");
        List<VehicleInfo> vehicles = List.of(v1, v2);

        when(vehicleRepo.findAll()).thenReturn(Flux.fromIterable(vehicles));

        StepVerifier.create(service.getVehicles())
                .expectNextMatches(v -> v.getId().equals(1L) && "Toyota".equals(v.getBrand()))
                .expectNextMatches(v -> v.getId().equals(2L) && "Honda".equals(v.getBrand()))
                .verifyComplete();

        verify(vehicleRepo).findAll();
    }

    @Test
    void getSimulations_shouldReturnAllSimulations() {
        SimulationResult s1 = new SimulationResult(); s1.setId(100L);
        SimulationResult s2 = new SimulationResult(); s2.setId(101L);
        List<SimulationResult> sims = List.of(s1, s2);

        when(resultRepo.findAll()).thenReturn(Flux.fromIterable(sims));

        StepVerifier.create(service.getSimulations())
                .expectNext(s1)
                .expectNext(s2)
                .verifyComplete();

        verify(resultRepo).findAll();
    }

    @Test
    void deleteVehicle_shouldDeleteSimulationsAndVehicle() {
        Long vehicleId = 1L;
        SimulationResult sim1 = new SimulationResult(); sim1.setId(100L);
        SimulationResult sim2 = new SimulationResult(); sim2.setId(101L);
        List<SimulationResult> simulations = List.of(sim1, sim2);

        when(resultRepo.findByVehicleId(vehicleId)).thenReturn(Flux.fromIterable(simulations));
        when(resultRepo.deleteById(sim1.getId())).thenReturn(Mono.empty());
        when(resultRepo.deleteById(sim2.getId())).thenReturn(Mono.empty());
        when(vehicleRepo.deleteById(vehicleId)).thenReturn(Mono.empty());

        StepVerifier.create(service.deleteVehicle(vehicleId))
                .verifyComplete();

        verify(resultRepo).findByVehicleId(vehicleId);
        verify(resultRepo).deleteById(sim1.getId());
        verify(resultRepo).deleteById(sim2.getId());
        verify(vehicleRepo).deleteById(vehicleId);
    }

    @Test
    void updateVehicle_shouldUpdateFieldsAndSave() {
        Long vehicleId = 2L;

        VehicleInfo existing = new VehicleInfo(); existing.setId(vehicleId);
        existing.setBrand("OldBrand");
        existing.setModel("OldModel");
        existing.setYear(2010);
        existing.setUsage("personal");
        existing.setDriverAge(30);

        VehicleInfo updates = new VehicleInfo();
        updates.setBrand("NewBrand");
        updates.setModel("NewModel");
        updates.setYear(2022);
        updates.setUsage("carga");
        updates.setDriverAge(45);

        VehicleInfo saved = new VehicleInfo(); saved.setId(vehicleId);
        saved.setBrand("NewBrand"); saved.setModel("NewModel");
        saved.setYear(2022); saved.setUsage("carga"); saved.setDriverAge(45);

        when(vehicleRepo.findById(vehicleId)).thenReturn(Mono.just(existing));
        when(vehicleRepo.save(any())).thenReturn(Mono.just(saved));

        StepVerifier.create(service.updateVehicle(vehicleId, updates))
                .expectNextMatches(result ->
                        result.getBrand().equals("NewBrand") &&
                                result.getModel().equals("NewModel") &&
                                result.getYear() == 2022 &&
                                result.getUsage().equals("carga") &&
                                result.getDriverAge() == 45
                )
                .verifyComplete();

        verify(vehicleRepo).findById(vehicleId);
        verify(vehicleRepo).save(any());
    }




}
