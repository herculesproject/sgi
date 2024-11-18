
-- TIPO ENLACE
INSERT INTO test.tipo_enlace (id, activo) VALUES (1, true);
INSERT INTO test.tipo_enlace (id, activo) VALUES (2, true);

-- TIPO_ENLACE_NOMBRE
INSERT INTO test.tipo_enlace_nombre (tipo_enlace_id, lang, value_) VALUES(1, 'es', 'nombre-001');
INSERT INTO test.tipo_enlace_nombre (tipo_enlace_id, lang, value_) VALUES(2, 'es', 'nombre-002');

-- TIPO_ENLACE_DESCRIPCION
INSERT INTO test.tipo_enlace_descripcion (tipo_enlace_id, lang, value_) VALUES(1, 'es', 'descripcion-001');
INSERT INTO test.tipo_enlace_descripcion (tipo_enlace_id, lang, value_) VALUES(2, 'es', 'descripcion-002');

-- MODELO EJECUCION
INSERT INTO test.modelo_ejecucion (id, nombre, descripcion, activo) VALUES (1, 'nombre-001', 'descripcion-001', true);

-- MODELO TIPO ENLACE
INSERT INTO test.modelo_tipo_enlace (id, tipo_enlace_id, modelo_ejecucion_id, activo) VALUES (1, 1, 1, true);
INSERT INTO test.modelo_tipo_enlace (id, tipo_enlace_id, modelo_ejecucion_id, activo) VALUES (2, 2, 1, true);