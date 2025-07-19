--table vehiculo informacion
CREATE TABLE vehicle_info (
    id SERIAL PRIMARY KEY,
    brand VARCHAR(50),--marca
    model VARCHAR(50),--modelo
    year INT,--año del vehiculo|
    usage VARCHAR(20),--tipo de uso
    driver_age INT,--edad conductor
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP--fecha de registro
);
--table resultado simulacion
CREATE TABLE simulation_result (
    id SERIAL PRIMARY KEY,
    vehicle_id INT REFERENCES vehicle_info(id),
    base_premium NUMERIC(10,2),--prima base 
    adjustment NUMERIC(10,2),--ajustes aplicados
    total_premium NUMERIC(10,2),--total estimado
    calculated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP--fecha de registro
);