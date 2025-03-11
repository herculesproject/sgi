-- SECTOR APLICACION
INSERT INTO test.sector_aplicacion
(id, activo, descripcion) VALUES
(1, TRUE, 'descripcion-sector-aplicacion-001'),
(2, TRUE, 'descripcion-sector-aplicacion-002'),
(3, FALSE, 'descripcion-sector-aplicacion-003');

-- SECTOR APLICACION NOMBRE
INSERT INTO test.sector_aplicacion_nombre
(sector_aplicacion_id, lang, value_) VALUES
(1, 'es', 'nombre-sector-aplicacion-001'),
(2, 'es', 'nombre-sector-aplicacion-002'),
(3, 'es', 'nombre-sector-aplicacion-003');

ALTER SEQUENCE test.sector_aplicacion_seq RESTART WITH 4;
