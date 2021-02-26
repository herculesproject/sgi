
-- MODELO EJECUCION
INSERT INTO csp.modelo_ejecucion (id, nombre, descripcion, activo) VALUES (1, 'nombre-1', 'descripcion-1', true);

-- MODELO UNIDAD
INSERT INTO csp.modelo_unidad (id, unidad_gestion_ref, modelo_ejecucion_id, activo) VALUES (1, 'unidad-001', 1, true);

-- TIPO_FINALIDAD
INSERT INTO csp.tipo_finalidad (id,nombre,descripcion,activo) VALUES (1,'nombre-1','descripcion-1',true);

-- MODELO TIPO FINALIDAD
INSERT INTO csp.modelo_tipo_finalidad (id, modelo_ejecucion_id, tipo_finalidad_id, activo) VALUES (1, 1, 1, true);

-- TIPO_REGIMEN_CONCURRENCIA
INSERT INTO csp.tipo_regimen_concurrencia (id,nombre,activo) VALUES (1,'nombre-1',true);

-- TIPO AMBITO GEOGRAFICO
INSERT INTO csp.tipo_ambito_geografico (id, nombre, activo) VALUES (1, 'nombre-001', true);

-- CONVOCATORIA
INSERT INTO csp.convocatoria
(id, unidad_gestion_ref, modelo_ejecucion_id, codigo, anio, titulo, objeto, observaciones, tipo_finalidad_id, tipo_regimen_concurrencia_id, destinatarios, colaborativos, estado, duracion, tipo_ambito_geografico_id, clasificacion_cvn, activo)
VALUES(1, 'OPE', 1, 'codigo-001', 2020, 'titulo-001', 'objeto-001', 'observaciones-001', 1, 1, 'INDIVIDUAL', true, 'REGISTRADA', 12, 1, 'AYUDAS', true);

-- TIPO FASE
INSERT INTO csp.tipo_fase (id, nombre, descripcion, activo) VALUES (1, 'nombre-001', 'descripcion-001', true);

--CONVOCATORIA FASE
INSERT INTO csp.convocatoria_fase(id, convocatoria_id, tipo_fase_id, fecha_inicio, fecha_fin, observaciones) VALUES (1, 1, 1, '2020-10-01', '2020-10-15', 'observaciones-1');

-- CONFIGURACION SOLICITUD
INSERT INTO csp.configuracion_solicitud 
(id, convocatoria_id, tramitacion_sgi, convocatoria_fase_id, importe_maximo_solicitud, formulario_solicitud) 
VALUES(1, 1, TRUE, 1, 12345, 'ESTANDAR');

-- PROGRAMA
INSERT INTO csp.programa (id, nombre, descripcion, programa_padre_id, activo) VALUES (1, 'nombre-001', 'descripcion-001', null, true);
INSERT INTO csp.programa (id, nombre, descripcion, programa_padre_id, activo) VALUES (2, 'nombre-002', 'descripcion-002', 1, true);
INSERT INTO csp.programa (id, nombre, descripcion, programa_padre_id, activo) VALUES (3, 'nombre-003', 'descripcion-003', 1, true);

-- CONVOCATORIA ENTIDAD CONVOCANTE
INSERT INTO csp.convocatoria_entidad_convocante (id,  convocatoria_id, entidad_ref, programa_id) VALUES (1, 1, 'entidad-001', 1);
INSERT INTO csp.convocatoria_entidad_convocante (id,  convocatoria_id, entidad_ref, programa_id) VALUES (2, 1, 'entidad-002', 1);


-- ESTADO SOLICITUD
INSERT INTO csp.estado_solicitud (id, id_solicitud, estado, fecha_estado, comentario) VALUES (1, 1, 'BORRADOR', '2020-11-17 15:00', 'comentario');

-- SOLICITUD
INSERT INTO csp.solicitud (id, codigo_externo, codigo_registro_interno, estado_solicitud_id, convocatoria_id, creador_ref, solicitante_ref, observaciones, convocatoria_externa, unidad_gestion_ref, formulario_solicitud, activo)
 VALUES (1, null, 'SGI_SLC1202011061027', 1, 1, 'usr-001', 'usr-002', 'observaciones 1', null, 'OPE', 'ESTANDAR', true);

-- SOLICITUD PROYECTO DATOS
INSERT INTO csp.solicitud_proyecto_datos (id, solicitud_id, titulo, colaborativo, presupuesto_por_entidades ) 
VALUES (1, 1, 'titulo-1', true, true);

-- ROL SOCIO
INSERT INTO csp.rol_socio (id, abreviatura, nombre, descripcion, coordinador, activo) VALUES(1, '001', 'nombre-001', 'descripcion-001' , false, false);

-- SOLICITUD PROYECTO SOCIO
INSERT INTO csp.solicitud_proyecto_socio (id, solicitud_proyecto_datos_id, rol_socio_id, mes_inicio, mes_fin, num_investigadores, importe_solicitado, empresa_ref) 
VALUES (1, 1, 1, 2, 6, 8, 4, '001');
