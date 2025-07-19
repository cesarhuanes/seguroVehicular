package com.segurovehiculo.model;

import com.segurovehiculo.domain.VehicleInfo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleInfoRepository extends ReactiveCrudRepository<VehicleInfo, Long> {
}
