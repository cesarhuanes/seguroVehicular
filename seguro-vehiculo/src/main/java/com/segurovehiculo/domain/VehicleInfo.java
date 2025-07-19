package com.segurovehiculo.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("vehicle_info")
public class VehicleInfo {
    @Id
    private Long id;

    private String brand;
    private String model;
    private int year;
    private String usage;

    @Column("driver_age")
    private int driverAge;

    @Column("created_at")
    private LocalDateTime createdAt;
}
