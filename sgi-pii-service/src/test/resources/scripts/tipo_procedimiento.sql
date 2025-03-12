-- TIPO PROCEDIMIENTO
INSERT INTO test.tipo_procedimiento (id, activo, descripcion)
VALUES 
(1, TRUE, 'descripcion-001'),
(2, TRUE, 'descripcion-002'),
(3, TRUE, 'descripcion-003');

-- TIPO PROCEDIMIENTO NOMBRE
INSERT INTO test.tipo_procedimiento_nombre(tipo_procedimiento_id, lang, value_) 
VALUES
(1, 'es', 'nombre-001'),
(2, 'es', 'nombre-002'),
(3, 'es', 'nombre-003');

ALTER SEQUENCE test.tipo_procedimiento_seq RESTART WITH 4;
