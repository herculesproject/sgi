-- TIPO FASE
INSERT INTO test.tipo_fase (id, descripcion, activo) VALUES (1, 'descripcion-001', true);
INSERT INTO test.tipo_fase (id, descripcion, activo) VALUES (2, 'descripcion-002', true);

-- TIPO_FASE_NOMBRE
INSERT INTO test.tipo_fase_nombre (tipo_fase_id, lang, value_) VALUES (1,  'es', 'nombre-001');
INSERT INTO test.tipo_fase_nombre (tipo_fase_id, lang, value_) VALUES (2,  'es', 'nombre-002');

-- MODELO EJECUCION
INSERT INTO test.modelo_ejecucion (id, nombre, descripcion, activo) VALUES (1, 'nombre-001', 'descripcion-001', true);
