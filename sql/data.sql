-- Insertar registros en vehicle_info
INSERT INTO vehicle_info (brand, model, year, usage, driver_age, created_at) VALUES
('Toyota', 'Corolla', 2018, 'personal', 45, CURRENT_TIMESTAMP),
('BMW', 'X5', 2022, 'trabajo', 38, CURRENT_TIMESTAMP),
('Ford', 'F-150', 2014, 'carga', 52, CURRENT_TIMESTAMP),
('Audi', 'A4', 2020, 'personal', 29, CURRENT_TIMESTAMP),
('Hyundai', 'Elantra', 2016, 'carga', 61, CURRENT_TIMESTAMP);

-- Insertar registros en simulation_result (asumiendo IDs de vehicle_info del 1 al 5)
INSERT INTO simulation_result (vehicle_id, base_premium, adjustment, total_premium, calculated_at) VALUES
(1, 500.00, 75.00, 575.00, CURRENT_TIMESTAMP),   -- Toyota: año > 2015 → +15%
(2, 500.00, 155.00, 655.00, CURRENT_TIMESTAMP),  -- BMW: año > 2015 +15%, marca +20%, uso trabajo
(3, 500.00, 45.00, 545.00, CURRENT_TIMESTAMP),   -- Ford: carga +10%, edad > 50 -5%
(4, 500.00, 125.00, 625.00, CURRENT_TIMESTAMP),  -- Audi: año > 2015 +15%, marca +10%
(5, 500.00, 80.00, 580.00, CURRENT_TIMESTAMP);   -- Hyundai: año > 2015 +15%, carga +10%, edad > 50 -5%