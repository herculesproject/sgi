-- MODELO_EJECUCION
INSERT INTO test.modelo_ejecucion (id, activo) VALUES (1, true);

-- MODELO_EJECUCION_NOMBRE
INSERT INTO test.modelo_ejecucion_nombre (modelo_ejecucion_id, lang, value_) VALUES (1, 'es', 'nombre-1');

-- MODELO_EJECUCION_DESCRIPCION
INSERT INTO test.modelo_ejecucion_descripcion (modelo_ejecucion_id, lang, value_) VALUES (1, 'es', 'descripcion-1');

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
VALUES(1, '2', 1, 'codigo-001', '2021-10-15T23:59:59Z', '2021-10-16T23:59:59Z', '2021-10-17T23:59:59Z', 'titulo-001', 'objeto-001', 'observaciones-001', 1, 1, 'REGISTRADA', 12, 1, 'AYUDAS', true, 'PROYECTO');

-- TIPO FASE
INSERT INTO test.tipo_fase (id, activo) VALUES (1, true);

-- TIPO_FASE_NOMBRE
INSERT INTO test.tipo_fase_nombre (tipo_fase_id, lang, value_) VALUES (1, 'es', 'nombre-001');

-- TIPO_FASE_DESCRIPCION
INSERT INTO test.tipo_fase_descripcion (tipo_fase_id, lang, value_) VALUES (1, 'es', 'descripcion-001');

--CONVOCATORIA FASE
INSERT INTO test.convocatoria_fase(id, convocatoria_id, tipo_fase_id, fecha_inicio, fecha_fin) VALUES (1, 1, 1, '2020-10-01T00:00:00Z', '2020-10-15T23:59:59Z');

-- CONVOCATORIA FASE OBSERVACIONES
INSERT INTO test.convocatoria_fase_observaciones (convocatoria_fase_id, lang, value_) VALUES (1, 'es', 'observaciones-1');

-- CONFIGURACION SOLICITUD
INSERT INTO test.configuracion_solicitud 
(id, convocatoria_id, tramitacion_sgi, convocatoria_fase_id, importe_maximo_solicitud) 
VALUES(1, 1, TRUE, 1, 12345);

-- PROGRAMA
INSERT INTO test.programa (id, programa_padre_id, activo) VALUES (1, null, true);
INSERT INTO test.programa (id, programa_padre_id, activo) VALUES (2, 1,    true);
INSERT INTO test.programa (id, programa_padre_id, activo) VALUES (3, 1,    true);

-- PROGRAMA_NOMBRE
INSERT INTO test.programa_nombre (programa_id, lang, value_) VALUES (1,  'es', 'nombre-001');
INSERT INTO test.programa_nombre (programa_id, lang, value_) VALUES (2,  'es', 'nombre-002');
INSERT INTO test.programa_nombre (programa_id, lang, value_) VALUES (3,  'es', 'nombre-003');

-- PROGRAMA_DESCRIPCION
INSERT INTO test.programa_descripcion (programa_id, lang, value_) VALUES (1,  'es', 'descripcion-001');
INSERT INTO test.programa_descripcion (programa_id, lang, value_) VALUES (2,  'es', 'descripcion-002');
INSERT INTO test.programa_descripcion (programa_id, lang, value_) VALUES (3,  'es', 'descripcion-003');

-- CONVOCATORIA ENTIDAD CONVOCANTE
INSERT INTO test.convocatoria_entidad_convocante (id,  convocatoria_id, entidad_ref, programa_id) VALUES (1, 1, 'entidad-001', 1);
INSERT INTO test.convocatoria_entidad_convocante (id,  convocatoria_id, entidad_ref, programa_id) VALUES (2, 1, 'entidad-002', 1);

-- SOLICITUD
INSERT INTO test.solicitud (id, titulo,codigo_externo, codigo_registro_interno, estado_solicitud_id, convocatoria_id, creador_ref, solicitante_ref, observaciones, convocatoria_externa, unidad_gestion_ref, formulario_solicitud, activo)
 VALUES (1, 'titulo',null, 'SGI_SLC1202011061027', null, 1, 'usr-001', 'usr-002', 'observaciones 1', null, '2', 'PROYECTO', true);

-- ESTADO SOLICITUD
INSERT INTO test.estado_solicitud (id, solicitud_id, estado, fecha_estado, comentario) VALUES (1, 1, 'BORRADOR', '2020-11-17T00:00:00Z', 'comentario');

-- UPDATE SOLICITUD
UPDATE test.solicitud SET estado_solicitud_id = 1 WHERE id = 1;

-- SOLICITUD PROYECTO DATOS
INSERT INTO test.solicitud_proyecto (id, colaborativo, tipo_presupuesto) 
VALUES (1, true, 'GLOBAL');

-- ROL SOCIO
INSERT INTO test.rol_socio (id, coordinador, activo) VALUES(1, false, false);

INSERT INTO test.rol_socio_nombre (rol_socio_id, lang, value_) VALUES(1, 'es', 'nombre-001');

INSERT INTO test.rol_socio_descripcion (rol_socio_id, lang, value_) VALUES(1, 'es', 'descripcion-001');

INSERT INTO test.rol_socio_abreviatura (rol_socio_id, lang, value_) VALUES(1, 'es', '001');

-- SOLICITUD PROYECTO SOCIO
INSERT INTO test.solicitud_proyecto_socio (id, solicitud_proyecto_id, rol_socio_id, mes_inicio, mes_fin, num_investigadores, importe_solicitado, empresa_ref) 
VALUES (1, 1, 1, 2, 6, 8, 4, '001');
INSERT INTO test.solicitud_proyecto_socio (id, solicitud_proyecto_id, rol_socio_id, mes_inicio, mes_fin, num_investigadores, importe_solicitado, empresa_ref) 
VALUES (2, 1, 1, 2, 6, 8, 4, '002');

-- SOLICITUD PROYECTO PERIODO PAGO
INSERT INTO test.solicitud_proyecto_socio_periodo_pago (id, solicitud_proyecto_socio_id, num_periodo, importe, mes)
VALUES (1, 1, 3, 789, 3);
INSERT INTO test.solicitud_proyecto_socio_periodo_pago (id, solicitud_proyecto_socio_id, num_periodo, importe, mes)
VALUES (2, 2, 3, 365, 4);
INSERT INTO test.solicitud_proyecto_socio_periodo_pago (id, solicitud_proyecto_socio_id, num_periodo, importe, mes)
VALUES (3, 2, 3, 256, 5);