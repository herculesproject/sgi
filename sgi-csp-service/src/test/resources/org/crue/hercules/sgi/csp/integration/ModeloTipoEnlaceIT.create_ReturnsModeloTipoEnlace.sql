
-- TIPO ENLACE
INSERT INTO test.tipo_enlace (id, descripcion, activo) VALUES (1, 'descripcion-001', true);
INSERT INTO test.tipo_enlace (id, descripcion, activo) VALUES (2, 'descripcion-002', true);

-- TIPO_ENLACE_NOMBRE
INSERT INTO test.tipo_enlace_nombre (tipo_enlace_id, lang, value_) VALUES(1, 'es', 'nombre-001');
INSERT INTO test.tipo_enlace_nombre (tipo_enlace_id, lang, value_) VALUES(2, 'es', 'nombre-002');

-- MODELO EJECUCION
INSERT INTO test.modelo_ejecucion (id, nombre, descripcion, activo) VALUES (1, 'nombre-001', 'descripcion-001', true);
