
-- TIPO ENLACE
INSERT INTO test.tipo_enlace (id, activo) VALUES (1, true);
INSERT INTO test.tipo_enlace (id, activo) VALUES (2, true);

-- TIPO_ENLACE_NOMBRE
INSERT INTO test.tipo_enlace_nombre (tipo_enlace_id, lang, value_) VALUES(1, 'es', 'nombre-001');
INSERT INTO test.tipo_enlace_nombre (tipo_enlace_id, lang, value_) VALUES(2, 'es', 'nombre-002');

-- TIPO_ENLACE_DESCRIPCION
INSERT INTO test.tipo_enlace_descripcion (tipo_enlace_id, lang, value_) VALUES(1, 'es', 'descripcion-001');
INSERT INTO test.tipo_enlace_descripcion (tipo_enlace_id, lang, value_) VALUES(2, 'es', 'descripcion-002');

-- MODELO_EJECUCION
INSERT INTO test.modelo_ejecucion (id, descripcion, activo) VALUES (1, 'descripcion-001', true);

-- MODELO_EJECUCION_NOMBRE
INSERT INTO test.modelo_ejecucion_nombre (modelo_ejecucion_id, lang, value_) VALUES (1, 'es', 'nombre-001');

-- MODELO TIPO ENLACE
INSERT INTO test.modelo_tipo_enlace (id, tipo_enlace_id, modelo_ejecucion_id, activo) VALUES (1, 1, 1, true);
INSERT INTO test.modelo_tipo_enlace (id, tipo_enlace_id, modelo_ejecucion_id, activo) VALUES (2, 2, 1, true);