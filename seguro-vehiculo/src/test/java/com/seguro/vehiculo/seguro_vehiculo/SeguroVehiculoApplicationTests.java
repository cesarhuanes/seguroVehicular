package com.seguro.vehiculo.seguro_vehiculo;

import com.segurovehiculo.RedisConfig;
import com.segurovehiculo.SeguroVehiculoApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {SeguroVehiculoApplication.class, RedisConfig.class})
class SeguroVehiculoApplicationTests {

	@Test
	void contextLoads() {
	}

}
