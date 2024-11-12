-- MODELO EJECUCION
INSERT INTO test.modelo_ejecucion (id, nombre, descripcion, activo) VALUES (1, 'nombre-me-1', 'descripcion-me-1', true);

-- TIPO HITO
INSERT INTO test.tipo_hito (id, activo) VALUES (1, true);

-- TIPO HITO NOMBRE
INSERT INTO test.tipo_hito_nombre (tipo_hito_id, lang, value_) VALUES (1, 'es', 'nombre-th-1');

-- TIPO HITO DESCRIPCIÓN
INSERT INTO test.tipo_hito_descripcion (tipo_hito_id, lang, value_) VALUES (1, 'es', 'descripcion-th-1');

-- MODELO TIPO HITO
INSERT INTO test.modelo_tipo_hito (id, modelo_ejecucion_id, tipo_hito_id, solicitud, proyecto, convocatoria, activo) VALUES (1, 1, 1, true, true, true, true);
