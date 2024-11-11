-- MODELO EJECUCION
INSERT INTO test.modelo_ejecucion (id, nombre, descripcion, activo) VALUES (1, 'nombre-me-1', 'descripcion-me-1', true);

-- TIPO HITO
INSERT INTO test.tipo_hito (id, descripcion, activo) VALUES (1, 'descripcion-th-1', true);

-- TIPO HITO NOMBRE
INSERT INTO test.tipo_hito_nombre (tipo_hito_id, lang, value_) VALUES (1, 'es', 'nombre-th-1');
