-- MODELO_EJECUCION
INSERT INTO test.modelo_ejecucion (id, descripcion, activo) VALUES (1, 'descripcion-1', true);

-- MODELO_EJECUCION_NOMBRE
INSERT INTO test.modelo_ejecucion_nombre (modelo_ejecucion_id, lang, value_) VALUES (1, 'es', 'nombre-1');

-- MODELO UNIDAD
INSERT INTO test.modelo_unidad (id, unidad_gestion_ref, modelo_ejecucion_id, activo) VALUES (1, 'unidad-001', 1, true);

-- TIPO_FINALIDAD
INSERT INTO test.tipo_finalidad (id, activo) VALUES (1, true);

-- TIPO_FINALIDAD_NOMBRE
INSERT INTO test.tipo_finalidad_nombre (tipo_finalidad_id, lang, value_) VALUES(1, 'es', 'nombre-1');

-- TIPO_FINALIDAD_DESCRIPCION
INSERT INTO test.tipo_finalidad_descripcion (tipo_finalidad_id, lang, value_) VALUES(1, 'es', 'descripcion-1');

-- MODELO TIPO FINALIDAD
INSERT INTO test.modelo_tipo_finalidad (id, modelo_ejecucion_id, tipo_finalidad_id, activo) VALUES (1, 1, 1, true);

-- TIPO_REGIMEN_CONCURRENCIA
INSERT INTO test.tipo_regimen_concurrencia (id,activo) VALUES (1,true);

-- TIPO_REGIMEN_CONCURRENCIA_NOMBRE
INSERT INTO test.tipo_regimen_concurrencia_nombre (tipo_regimen_concurrencia_id, lang, value_) VALUES(1, 'es', 'nombre-1');

-- TIPO AMBITO GEOGRAFICO
INSERT INTO test.tipo_ambito_geografico (id, activo) VALUES (1, true);

INSERT INTO test.tipo_ambito_geografico_nombre (tipo_ambito_geografico_id, lang, value_) VALUES(1, 'es', 'nombre-001');

-- CONVOCATORIA
INSERT INTO test.convocatoria
(id, unidad_gestion_ref, modelo_ejecucion_id, codigo, fecha_publicacion, fecha_provisional, fecha_concesion, titulo, objeto, observaciones, tipo_finalidad_id, tipo_regimen_concurrencia_id, estado, duracion, tipo_ambito_geografico_id, clasificacion_cvn, activo, formulario_solicitud)
VALUES(1, 'unidad-001', 1, 'codigo-001', '2021-10-15T23:59:59Z', '2021-10-16T23:59:59Z', '2021-10-17T23:59:59Z', 'titulo-001', 'objeto-001', 'observaciones-001', 1, 1, 'REGISTRADA', 12, 1, 'AYUDAS', true, 'PROYECTO');
INSERT INTO test.convocatoria
(id, unidad_gestion_ref, modelo_ejecucion_id, codigo, fecha_publicacion, fecha_provisional, fecha_concesion, titulo, objeto, observaciones, tipo_finalidad_id, tipo_regimen_concurrencia_id, estado, duracion, tipo_ambito_geografico_id, clasificacion_cvn, activo, formulario_solicitud)
VALUES(2, 'unidad-001', 1, 'codigo-002', '2021-10-15T23:59:59Z', '2021-10-16T23:59:59Z', '2021-10-17T23:59:59Z', 'titulo-002', 'objeto-002', 'observaciones-002', 1, 1, 'REGISTRADA', 12, 1, 'AYUDAS', true, 'PROYECTO');

-- TIPO FASE
INSERT INTO test.tipo_fase (id, activo) VALUES (1, true);

-- TIPO_FASE_NOMBRE
INSERT INTO test.tipo_fase_nombre (tipo_fase_id, lang, value_) VALUES (1, 'es', 'nombre-001');

-- TIPO_FASE_DESCRIPCION
INSERT INTO test.tipo_fase_descripcion (tipo_fase_id, lang, value_) VALUES (1, 'es', 'descripcion-001');

--CONVOCATORIA FASE
INSERT INTO test.convocatoria_fase(id, convocatoria_id, tipo_fase_id, fecha_inicio, fecha_fin, observaciones) VALUES (1, 1, 1, '2020-10-01T00:00:00Z', '2020-10-15T23:59:59Z', 'observaciones-1');
INSERT INTO test.convocatoria_fase(id, convocatoria_id, tipo_fase_id, fecha_inicio, fecha_fin, observaciones) VALUES (2, 2, 1, '2020-10-01T00:00:00Z', '2020-10-15T23:59:59Z', 'observaciones-1');

-- CONFIGURACION SOLICITUD
INSERT INTO test.configuracion_solicitud 
(id, convocatoria_id, tramitacion_sgi, convocatoria_fase_id, importe_maximo_solicitud) 
VALUES(1, 1, TRUE, 1, 12345);
INSERT INTO test.configuracion_solicitud 
(id, convocatoria_id, tramitacion_sgi, convocatoria_fase_id, importe_maximo_solicitud) 
VALUES(2, 2, TRUE, 1, 12345);

-- MODELO TIPO FASE
INSERT INTO test.modelo_tipo_fase (id, tipo_fase_id, modelo_ejecucion_id, solicitud, convocatoria, proyecto, activo) VALUES (1, 1, 1, true, true, true, true);

-- TIPO DOCUMENTO
INSERT INTO test.tipo_documento (id, activo) VALUES (1, true);
INSERT INTO test.tipo_documento (id, activo) VALUES (2, true);
INSERT INTO test.tipo_documento (id, activo) VALUES (3, true);
INSERT INTO test.tipo_documento (id, activo) VALUES (4, true);
INSERT INTO test.tipo_documento (id, activo) VALUES (5, true);

-- TIPO_DOCUMENTO_NOMBRE
INSERT INTO test.tipo_documento_nombre (tipo_documento_id, lang, value_) VALUES (1, 'es', 'nombre-001');
INSERT INTO test.tipo_documento_nombre (tipo_documento_id, lang, value_) VALUES (2, 'es', 'nombre-002');
INSERT INTO test.tipo_documento_nombre (tipo_documento_id, lang, value_) VALUES (3, 'es', 'nombre-003');
INSERT INTO test.tipo_documento_nombre (tipo_documento_id, lang, value_) VALUES (4, 'es', 'nombre-004');
INSERT INTO test.tipo_documento_nombre (tipo_documento_id, lang, value_) VALUES (5, 'es', 'nombre-005');

-- TIPO_DOCUMENTO_DESCRIPCION
INSERT INTO test.tipo_documento_descripcion (tipo_documento_id, lang, value_) VALUES (1, 'es', 'descripcion-001');
INSERT INTO test.tipo_documento_descripcion (tipo_documento_id, lang, value_) VALUES (2, 'es', 'descripcion-002');
INSERT INTO test.tipo_documento_descripcion (tipo_documento_id, lang, value_) VALUES (3, 'es', 'descripcion-003');
INSERT INTO test.tipo_documento_descripcion (tipo_documento_id, lang, value_) VALUES (4, 'es', 'descripcion-004');
INSERT INTO test.tipo_documento_descripcion (tipo_documento_id, lang, value_) VALUES (5, 'es', 'descripcion-005');

-- MODELO TIPO DOCUMENTO
INSERT INTO test.modelo_tipo_documento (id, tipo_documento_id, modelo_ejecucion_id, modelo_tipo_fase_id, activo) VALUES (1, 1, 1, 1, true);
INSERT INTO test.modelo_tipo_documento (id, tipo_documento_id, modelo_ejecucion_id, modelo_tipo_fase_id, activo) VALUES (2, 2, 1, 1, true);
INSERT INTO test.modelo_tipo_documento (id, tipo_documento_id, modelo_ejecucion_id, modelo_tipo_fase_id, activo) VALUES (3, 3, 1, 1, true);
INSERT INTO test.modelo_tipo_documento (id, tipo_documento_id, modelo_ejecucion_id, modelo_tipo_fase_id, activo) VALUES (4, 4, 1, 1, true);
INSERT INTO test.modelo_tipo_documento (id, tipo_documento_id, modelo_ejecucion_id, modelo_tipo_fase_id, activo) VALUES (5, 5, 1, 1, true);

-- DOCUMENTO REQUERIDO SOLICITUD
INSERT INTO test.DOCUMENTO_REQUERIDO_SOLICITUD (ID, CONFIGURACION_SOLICITUD_ID, TIPO_DOCUMENTO_ID, OBSERVACIONES) VALUES(1, 1, 1, 'observaciones-001');
INSERT INTO test.DOCUMENTO_REQUERIDO_SOLICITUD (ID, CONFIGURACION_SOLICITUD_ID, TIPO_DOCUMENTO_ID, OBSERVACIONES) VALUES(2, 1, 2, 'observaciones-002');
INSERT INTO test.DOCUMENTO_REQUERIDO_SOLICITUD (ID, CONFIGURACION_SOLICITUD_ID, TIPO_DOCUMENTO_ID, OBSERVACIONES) VALUES(3, 1, 3, 'observaciones-003');
INSERT INTO test.DOCUMENTO_REQUERIDO_SOLICITUD (ID, CONFIGURACION_SOLICITUD_ID, TIPO_DOCUMENTO_ID, OBSERVACIONES) VALUES(4, 1, 4, 'observaciones-4');
INSERT INTO test.DOCUMENTO_REQUERIDO_SOLICITUD (ID, CONFIGURACION_SOLICITUD_ID, TIPO_DOCUMENTO_ID, OBSERVACIONES) VALUES(5, 1, 5, 'observaciones-5');
INSERT INTO test.DOCUMENTO_REQUERIDO_SOLICITUD (ID, CONFIGURACION_SOLICITUD_ID, TIPO_DOCUMENTO_ID, OBSERVACIONES) VALUES(6, 2, 1, 'observaciones-006');
INSERT INTO test.DOCUMENTO_REQUERIDO_SOLICITUD (ID, CONFIGURACION_SOLICITUD_ID, TIPO_DOCUMENTO_ID, OBSERVACIONES) VALUES(7, 2, 2, 'observaciones-007');
INSERT INTO test.DOCUMENTO_REQUERIDO_SOLICITUD (ID, CONFIGURACION_SOLICITUD_ID, TIPO_DOCUMENTO_ID, OBSERVACIONES) VALUES(8, 2, 3, 'observaciones-008');
INSERT INTO test.DOCUMENTO_REQUERIDO_SOLICITUD (ID, CONFIGURACION_SOLICITUD_ID, TIPO_DOCUMENTO_ID, OBSERVACIONES) VALUES(9, 2, 4, 'observaciones-9');
INSERT INTO test.DOCUMENTO_REQUERIDO_SOLICITUD (ID, CONFIGURACION_SOLICITUD_ID, TIPO_DOCUMENTO_ID, OBSERVACIONES) VALUES(10, 2, 5, 'observaciones-10');

