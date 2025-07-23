-- TIPO FASE
INSERT INTO test.tipo_fase (id, activo) VALUES (1, true);
INSERT INTO test.tipo_fase (id, activo) VALUES (2, true);

-- TIPO_FASE_NOMBRE
INSERT INTO test.tipo_fase_nombre (tipo_fase_id, lang, value_) VALUES (1, 'es', 'nombre-001');
INSERT INTO test.tipo_fase_nombre (tipo_fase_id, lang, value_) VALUES (2, 'es', 'nombre-002');

-- TIPO_FASE_DESCRIPCION
INSERT INTO test.tipo_fase_descripcion (tipo_fase_id, lang, value_) VALUES (1, 'es', 'descripcion-001');
INSERT INTO test.tipo_fase_descripcion (tipo_fase_id, lang, value_) VALUES (2, 'es', 'descripcion-002');

-- MODELO_EJECUCION
INSERT INTO test.modelo_ejecucion (id, descripcion, activo) VALUES (1, 'descripcion-001', true);

-- MODELO_EJECUCION_NOMBRE
INSERT INTO test.modelo_ejecucion_nombre (modelo_ejecucion_id, lang, value_) VALUES (1, 'es', 'nombre-001');
