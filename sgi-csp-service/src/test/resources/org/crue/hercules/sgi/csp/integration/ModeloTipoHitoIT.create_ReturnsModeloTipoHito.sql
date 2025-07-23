-- MODELO_EJECUCION
INSERT INTO test.modelo_ejecucion (id, descripcion, activo) VALUES (1, 'descripcion-me-1', true);

-- MODELO_EJECUCION_NOMBRE
INSERT INTO test.modelo_ejecucion_nombre (modelo_ejecucion_id, lang, value_) VALUES (1, 'es', 'nombre-me-1');

-- TIPO HITO
INSERT INTO test.tipo_hito (id, activo) VALUES (1, true);

-- TIPO HITO NOMBRE
INSERT INTO test.tipo_hito_nombre (tipo_hito_id, lang, value_) VALUES (1, 'es', 'nombre-th-1');

-- TIPO HITO DESCRIPCIÓN
INSERT INTO test.tipo_hito_descripcion (tipo_hito_id, lang, value_) VALUES (1, 'es', 'descripcion-th-1');
