package com.segurovehiculo.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("simulation_result")
public class SimulationResult {
    @Id
    private Long id;

    @Column("vehicle_id")
    private Long vehicleId; // Ya no se usa VehicleInfo directamente, sino su ID

    @Column("base_premium")
    private BigDecimal basePremium;

    @Column("adjustment")
    private BigDecimal adjustment;

    @Column("total_premium")
    private BigDecimal totalPremium;

    @Column("calculated_at")
    private LocalDateTime calculatedAt;
}
