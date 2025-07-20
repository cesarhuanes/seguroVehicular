package com.seguro.vehiculo.seguro_vehiculo.controller;

import com.segurovehiculo.SeguroVehiculoApplication;
import com.segurovehiculo.controller.InsuranceController;
import com.segurovehiculo.domain.SimulationResult;
import com.segurovehiculo.domain.VehicleInfo;
import com.segurovehiculo.service.InsuranceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = InsuranceController.class)
@ContextConfiguration(classes = SeguroVehiculoApplication.class)
public class InsuranceControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private InsuranceService insuranceService;

    @Test
    void createVehicle_shouldReturnSavedVehicle() {
        VehicleInfo input = new VehicleInfo();
        input.setId(1L);
        input.setBrand("Toyota");

        when(insuranceService.saveVehicle(input)).thenReturn(Mono.just(input));

        webTestClient.post()
                .uri("/api/insurance/vehicle")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(input)
                .exchange()
                .expectStatus().isOk()
                .expectBody(VehicleInfo.class)
                .isEqualTo(input);

        verify(insuranceService).saveVehicle(input);
    }

    @Test
    void simulate_shouldReturnSimulationResult() {
        VehicleInfo vehicle = new VehicleInfo();
        vehicle.setId(1L);

        SimulationResult result = new SimulationResult(10L, 1L, new BigDecimal("500"),
                BigDecimal.ZERO, new BigDecimal("500"), LocalDateTime.now());

        when(insuranceService.simulatePremium(vehicle)).thenReturn(Mono.just(result));

        webTestClient.post()
                .uri("/api/insurance/simulate")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(vehicle)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SimulationResult.class)
                .isEqualTo(result);

        verify(insuranceService).simulatePremium(vehicle);
    }

    @Test
    void getVehicles_shouldReturnList() {
        VehicleInfo v1 = new VehicleInfo(); v1.setId(1L); v1.setBrand("Toyota");
        VehicleInfo v2 = new VehicleInfo(); v2.setId(2L); v2.setBrand("Honda");

        when(insuranceService.getVehicles()).thenReturn(Flux.just(v1, v2));

        webTestClient.get()
                .uri("/api/insurance/vehicles")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(VehicleInfo.class)
                .contains(v1, v2);

        verify(insuranceService).getVehicles();
    }

    @Test
    void getSimulations_shouldReturnAll() {
        SimulationResult s1 = new SimulationResult(); s1.setId(100L);
        SimulationResult s2 = new SimulationResult(); s2.setId(101L);

        when(insuranceService.getSimulations()).thenReturn(Flux.just(s1, s2));

        webTestClient.get()
                .uri("/api/insurance/simulations")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(SimulationResult.class)
                .contains(s1, s2);

        verify(insuranceService).getSimulations();
    }

    @Test
    void update_shouldReturnUpdatedVehicle() {
        VehicleInfo update = new VehicleInfo();
        update.setId(1L);
        update.setBrand("BMW");

        when(insuranceService.updateVehicle(1L, update)).thenReturn(Mono.just(update));

        webTestClient.put()
                .uri("/api/insurance/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(update)
                .exchange()
                .expectStatus().isOk()
                .expectBody(VehicleInfo.class)
                .isEqualTo(update);

        verify(insuranceService).updateVehicle(1L, update);
    }

    @Test
    void delete_shouldReturnNoContent() {
        when(insuranceService.deleteVehicle(1L)).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/api/insurance/1")
                .exchange()
                .expectStatus().isOk();

        verify(insuranceService).deleteVehicle(1L);
    }
}
