-- TIPO DOCUMENTO
INSERT INTO test.tipo_documento (id, descripcion, activo) VALUES (1, 'descripcion-001', true);
INSERT INTO test.tipo_documento (id, descripcion, activo) VALUES (2, 'descripcion-002', true);
INSERT INTO test.tipo_documento (id, descripcion, activo) VALUES (3, 'descripcion-003', true);
INSERT INTO test.tipo_documento (id, descripcion, activo) VALUES (4, 'descripcion-004', false);
INSERT INTO test.tipo_documento (id, descripcion, activo) VALUES (10, 'descripcion-010', true);
INSERT INTO test.tipo_documento (id, descripcion, activo) VALUES (11, 'descripcion-011', true);
INSERT INTO test.tipo_documento (id, descripcion, activo) VALUES (12, 'descripcion-012', true);
INSERT INTO test.tipo_documento (id, descripcion, activo) VALUES (13, 'descripcion-013', true);
INSERT INTO test.tipo_documento (id, descripcion, activo) VALUES (14, 'descripcion-014', true);

-- TIPO_DOCUMENTO_NOMBRE
INSERT INTO test.tipo_documento_nombre (tipo_documento_id, lang, value_) VALUES (1, 'es', 'nombre-001');
INSERT INTO test.tipo_documento_nombre (tipo_documento_id, lang, value_) VALUES (2, 'es', 'nombre-002');
INSERT INTO test.tipo_documento_nombre (tipo_documento_id, lang, value_) VALUES (3, 'es', 'nombre-003');
INSERT INTO test.tipo_documento_nombre (tipo_documento_id, lang, value_) VALUES (4, 'es', 'nombre-004');
INSERT INTO test.tipo_documento_nombre (tipo_documento_id, lang, value_) VALUES (10, 'es', 'nombre-010');
INSERT INTO test.tipo_documento_nombre (tipo_documento_id, lang, value_) VALUES (11, 'es', 'nombre-011');
INSERT INTO test.tipo_documento_nombre (tipo_documento_id, lang, value_) VALUES (12, 'es', 'nombre-012');
INSERT INTO test.tipo_documento_nombre (tipo_documento_id, lang, value_) VALUES (13, 'es', 'nombre-013');
INSERT INTO test.tipo_documento_nombre (tipo_documento_id, lang, value_) VALUES (14, 'es', 'nombre-014');


-- MODELO EJECUCION
INSERT INTO test.modelo_ejecucion (id, nombre, descripcion, activo) VALUES (1, 'nombre-001', 'descripcion-001', true);
INSERT INTO test.modelo_ejecucion (id, nombre, descripcion, activo) VALUES (2, 'nombre-002', 'descripcion-002', true);

-- MODELO TIPO DOCUMENTO
INSERT INTO test.modelo_tipo_documento (id, tipo_documento_id, modelo_ejecucion_id, modelo_tipo_fase_id, activo) VALUES (1, 1, 1, null, true);
INSERT INTO test.modelo_tipo_documento (id, tipo_documento_id, modelo_ejecucion_id, modelo_tipo_fase_id, activo) VALUES (2, 2, 1, null, true);
INSERT INTO test.modelo_tipo_documento (id, tipo_documento_id, modelo_ejecucion_id, modelo_tipo_fase_id, activo) VALUES (3, 3, 1, null, true);
INSERT INTO test.modelo_tipo_documento (id, tipo_documento_id, modelo_ejecucion_id, modelo_tipo_fase_id, activo) VALUES (4, 4, 1, null, true);
INSERT INTO test.modelo_tipo_documento (id, tipo_documento_id, modelo_ejecucion_id, modelo_tipo_fase_id, activo) VALUES (5, 10, 1, null, true);
INSERT INTO test.modelo_tipo_documento (id, tipo_documento_id, modelo_ejecucion_id, modelo_tipo_fase_id, activo) VALUES (6, 1, 2, null, true);
INSERT INTO test.modelo_tipo_documento (id, tipo_documento_id, modelo_ejecucion_id, modelo_tipo_fase_id, activo) VALUES (7, 11, 2, null, true);
INSERT INTO test.modelo_tipo_documento (id, tipo_documento_id, modelo_ejecucion_id, modelo_tipo_fase_id, activo) VALUES (8, 12, 2, null, true);
INSERT INTO test.modelo_tipo_documento (id, tipo_documento_id, modelo_ejecucion_id, modelo_tipo_fase_id, activo) VALUES (9, 13, 2, null, true);
INSERT INTO test.modelo_tipo_documento (id, tipo_documento_id, modelo_ejecucion_id, modelo_tipo_fase_id, activo) VALUES (10, 14, 2, null, true);
