-- TIPO FASE
INSERT INTO test.tipo_fase (id, descripcion, activo) VALUES (1,  'descripcion-001', true);
INSERT INTO test.tipo_fase (id, descripcion, activo) VALUES (2,  'descripcion-002', true);
INSERT INTO test.tipo_fase (id, descripcion, activo) VALUES (3,  'descripcion-003', true);
INSERT INTO test.tipo_fase (id, descripcion, activo) VALUES (4,  'descripcion-004', true);
INSERT INTO test.tipo_fase (id, descripcion, activo) VALUES (5,  'descripcion-005', false);
INSERT INTO test.tipo_fase (id, descripcion, activo) VALUES (10, 'descripcion-010', true);
INSERT INTO test.tipo_fase (id, descripcion, activo) VALUES (11, 'descripcion-011', true);
INSERT INTO test.tipo_fase (id, descripcion, activo) VALUES (12, 'descripcion-012', true);
INSERT INTO test.tipo_fase (id, descripcion, activo) VALUES (13, 'descripcion-013', true);
INSERT INTO test.tipo_fase (id, descripcion, activo) VALUES (14, 'descripcion-014', true);

-- TIPO_FASE_NOMBRE
INSERT INTO test.tipo_fase_nombre (tipo_fase_id, lang, value_) VALUES (1,  'es', 'nombre-001');
INSERT INTO test.tipo_fase_nombre (tipo_fase_id, lang, value_) VALUES (2,  'es', 'nombre-002');
INSERT INTO test.tipo_fase_nombre (tipo_fase_id, lang, value_) VALUES (3,  'es', 'nombre-003');
INSERT INTO test.tipo_fase_nombre (tipo_fase_id, lang, value_) VALUES (4,  'es', 'nombre-004');
INSERT INTO test.tipo_fase_nombre (tipo_fase_id, lang, value_) VALUES (5,  'es', 'nombre-005');
INSERT INTO test.tipo_fase_nombre (tipo_fase_id, lang, value_) VALUES (10, 'es', 'nombre-010');
INSERT INTO test.tipo_fase_nombre (tipo_fase_id, lang, value_) VALUES (11, 'es', 'nombre-011');
INSERT INTO test.tipo_fase_nombre (tipo_fase_id, lang, value_) VALUES (12, 'es', 'nombre-012');
INSERT INTO test.tipo_fase_nombre (tipo_fase_id, lang, value_) VALUES (13, 'es', 'nombre-013');
INSERT INTO test.tipo_fase_nombre (tipo_fase_id, lang, value_) VALUES (14, 'es', 'nombre-014');

-- MODELO EJECUCION
INSERT INTO test.modelo_ejecucion (id, nombre, descripcion, activo) VALUES (1, 'nombre-001', 'descripcion-001', true);
INSERT INTO test.modelo_ejecucion (id, nombre, descripcion, activo) VALUES (2, 'nombre-002', 'descripcion-002', true);

-- MODELO TIPO FASE
INSERT INTO test.modelo_tipo_fase (id, tipo_fase_id, modelo_ejecucion_id, solicitud, convocatoria, proyecto, activo) VALUES (1, 1, 1, false, true, true, true);
INSERT INTO test.modelo_tipo_fase (id, tipo_fase_id, modelo_ejecucion_id, solicitud, convocatoria, proyecto, activo) VALUES (2, 2, 1, false, true, true, true);
INSERT INTO test.modelo_tipo_fase (id, tipo_fase_id, modelo_ejecucion_id, solicitud, convocatoria, proyecto, activo) VALUES (3, 3, 1, false, true, true, true);
INSERT INTO test.modelo_tipo_fase (id, tipo_fase_id, modelo_ejecucion_id, solicitud, convocatoria, proyecto, activo) VALUES (4, 5, 1, false, true, true, true);
INSERT INTO test.modelo_tipo_fase (id, tipo_fase_id, modelo_ejecucion_id, solicitud, convocatoria, proyecto, activo) VALUES (5, 10, 1, false, true, true, true);
INSERT INTO test.modelo_tipo_fase (id, tipo_fase_id, modelo_ejecucion_id, solicitud, convocatoria, proyecto, activo) VALUES (6, 1, 2, false, true, true, true);
INSERT INTO test.modelo_tipo_fase (id, tipo_fase_id, modelo_ejecucion_id, solicitud, convocatoria, proyecto, activo) VALUES (7, 11, 2, false, true, true, true);
INSERT INTO test.modelo_tipo_fase (id, tipo_fase_id, modelo_ejecucion_id, solicitud, convocatoria, proyecto, activo) VALUES (8, 12, 2, false, true, true, true);
INSERT INTO test.modelo_tipo_fase (id, tipo_fase_id, modelo_ejecucion_id, solicitud, convocatoria, proyecto, activo) VALUES (9, 13, 2, false, true, true, true);
INSERT INTO test.modelo_tipo_fase (id, tipo_fase_id, modelo_ejecucion_id, solicitud, convocatoria, proyecto, activo) VALUES (10, 14, 2, false, true, true, true);
INSERT INTO test.modelo_tipo_fase (id, tipo_fase_id, modelo_ejecucion_id, solicitud, convocatoria, proyecto, activo) VALUES (11, 4, 1, false, true, true, false);

