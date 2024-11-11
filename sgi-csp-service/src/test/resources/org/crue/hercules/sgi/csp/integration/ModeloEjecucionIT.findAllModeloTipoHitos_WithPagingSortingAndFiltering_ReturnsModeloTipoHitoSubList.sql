-- TIPO HITO
INSERT INTO test.tipo_hito (id, descripcion, activo) VALUES (1, 'descripcion-001', true);
INSERT INTO test.tipo_hito (id, descripcion, activo) VALUES (2, 'descripcion-002', true);
INSERT INTO test.tipo_hito (id, descripcion, activo) VALUES (3, 'descripcion-003', true);
INSERT INTO test.tipo_hito (id, descripcion, activo) VALUES (4, 'descripcion-004', false);
INSERT INTO test.tipo_hito (id, descripcion, activo) VALUES (10, 'descripcion-010', true);
INSERT INTO test.tipo_hito (id, descripcion, activo) VALUES (11, 'descripcion-011', true);
INSERT INTO test.tipo_hito (id, descripcion, activo) VALUES (12, 'descripcion-012', true);
INSERT INTO test.tipo_hito (id, descripcion, activo) VALUES (13, 'descripcion-013', true);
INSERT INTO test.tipo_hito (id, descripcion, activo) VALUES (14, 'descripcion-014', true);

-- TIPO HITO NOMBRE
INSERT INTO test.tipo_hito_nombre (tipo_hito_id, lang, value_) VALUES (1, 'es', 'nombre-001');
INSERT INTO test.tipo_hito_nombre (tipo_hito_id, lang, value_) VALUES (2, 'es', 'nombre-002');
INSERT INTO test.tipo_hito_nombre (tipo_hito_id, lang, value_) VALUES (3, 'es', 'nombre-003');
INSERT INTO test.tipo_hito_nombre (tipo_hito_id, lang, value_) VALUES (4, 'es', 'nombre-004');
INSERT INTO test.tipo_hito_nombre (tipo_hito_id, lang, value_) VALUES (10, 'es', 'nombre-0010');
INSERT INTO test.tipo_hito_nombre (tipo_hito_id, lang, value_) VALUES (11, 'es', 'nombre-0011');
INSERT INTO test.tipo_hito_nombre (tipo_hito_id, lang, value_) VALUES (12, 'es', 'nombre-0012');
INSERT INTO test.tipo_hito_nombre (tipo_hito_id, lang, value_) VALUES (13, 'es', 'nombre-0013');
INSERT INTO test.tipo_hito_nombre (tipo_hito_id, lang, value_) VALUES (14, 'es', 'nombre-0014');

-- MODELO EJECUCION
INSERT INTO test.modelo_ejecucion (id, nombre, descripcion, activo) VALUES (1, 'nombre-001', 'descripcion-001', true);
INSERT INTO test.modelo_ejecucion (id, nombre, descripcion, activo) VALUES (2, 'nombre-002', 'descripcion-002', true);

-- MODELO TIPO HITO
INSERT INTO test.modelo_tipo_hito (id, tipo_hito_id, modelo_ejecucion_id, solicitud, proyecto, convocatoria, activo) VALUES (1, 1, 1, true, true, true, true);
INSERT INTO test.modelo_tipo_hito (id, tipo_hito_id, modelo_ejecucion_id, solicitud, proyecto, convocatoria, activo) VALUES (2, 2, 1, true, true, true, true);
INSERT INTO test.modelo_tipo_hito (id, tipo_hito_id, modelo_ejecucion_id, solicitud, proyecto, convocatoria, activo) VALUES (3, 3, 1, true, true, true, true);
INSERT INTO test.modelo_tipo_hito (id, tipo_hito_id, modelo_ejecucion_id, solicitud, proyecto, convocatoria, activo) VALUES (4, 10, 1, true, true, true, true);
INSERT INTO test.modelo_tipo_hito (id, tipo_hito_id, modelo_ejecucion_id, solicitud, proyecto, convocatoria, activo) VALUES (5, 1, 2, true, true, true, true);
INSERT INTO test.modelo_tipo_hito (id, tipo_hito_id, modelo_ejecucion_id, solicitud, proyecto, convocatoria, activo) VALUES (6, 11, 2, true, true, true, true);
INSERT INTO test.modelo_tipo_hito (id, tipo_hito_id, modelo_ejecucion_id, solicitud, proyecto, convocatoria, activo) VALUES (7, 12, 2, true, true, true, true);
INSERT INTO test.modelo_tipo_hito (id, tipo_hito_id, modelo_ejecucion_id, solicitud, proyecto, convocatoria, activo) VALUES (8, 13, 2, true, true, true, true);
INSERT INTO test.modelo_tipo_hito (id, tipo_hito_id, modelo_ejecucion_id, solicitud, proyecto, convocatoria, activo) VALUES (9, 14, 2, true, true, true, true);
INSERT INTO test.modelo_tipo_hito (id, tipo_hito_id, modelo_ejecucion_id, solicitud, proyecto, convocatoria, activo) VALUES (10, 11, 1, true, true, true, false);
INSERT INTO test.modelo_tipo_hito (id, tipo_hito_id, modelo_ejecucion_id, solicitud, proyecto, convocatoria, activo) VALUES (11, 4, 1, true, true, true, true);
