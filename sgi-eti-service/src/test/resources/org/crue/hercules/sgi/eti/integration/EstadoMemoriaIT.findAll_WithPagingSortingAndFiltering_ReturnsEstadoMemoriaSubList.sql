-- COMITÉ
INSERT INTO eti.comite (id, comite, activo) VALUES (2, 'Comite2', true);

-- TIPO ACTIVIDAD 
INSERT INTO eti.tipo_actividad (id, nombre, activo) VALUES (1, 'TipoActividad1', true);

-- TIPO MEMORIA 
INSERT INTO eti.tipo_memoria (id, nombre, activo) VALUES (1, 'TipoMemoria1', true);

--PETICION EVALUACION
INSERT INTO eti.peticion_evaluacion (id, titulo, codigo, solicitud_convocatoria_ref, tipo_actividad_id, fuente_financiacion, fecha_inicio, fecha_fin, resumen, valor_social, otro_valor_social, objetivos, dis_metodologico, externo, tiene_fondos_propios, usuario_ref, activo)
VALUES(1, 'PeticionEvaluacion1', 'Codigo', 'Ref solicitud convocatoria', 1, 'Fuente financiadora', '2020-07-09', '2021-07-09', 'Resumen', 3, 'Otro valor social', 'Objetivos', 'Metodologico', false, false, 'user-001', true);

 -- TIPO ESTADO MEMORIA 
INSERT INTO eti.tipo_estado_memoria (id, nombre, activo) VALUES (1, 'TipoEstadoMemoria1', true);
INSERT INTO eti.tipo_estado_memoria (id, nombre, activo) VALUES (2, 'TipoEstadoMemoria2', true);
INSERT INTO eti.tipo_estado_memoria (id, nombre, activo) VALUES (3, 'TipoEstadoMemoria3', true);
INSERT INTO eti.tipo_estado_memoria (id, nombre, activo) VALUES (4, 'TipoEstadoMemoria4', true);
INSERT INTO eti.tipo_estado_memoria (id, nombre, activo) VALUES (5, 'TipoEstadoMemoria5', true);
INSERT INTO eti.tipo_estado_memoria (id, nombre, activo) VALUES (6, 'TipoEstadoMemoria6', true);
INSERT INTO eti.tipo_estado_memoria (id, nombre, activo) VALUES (7, 'TipoEstadoMemoria7', true);
INSERT INTO eti.tipo_estado_memoria (id, nombre, activo) VALUES (8, 'TipoEstadoMemoria8', true);

-- MEMORIA 
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, usuario_ref, tipo_memoria_id, fecha_envio_secretaria, requiere_retrospectiva, fecha_retrospectiva, version, estado_actual_id)
 VALUES (1, 'ref-5588', 1, 2, 'Memoria001', 'userref-55698', 1, null, false, null, 1, 1);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, usuario_ref, tipo_memoria_id, fecha_envio_secretaria, requiere_retrospectiva, fecha_retrospectiva, version, estado_actual_id)
 VALUES (2, 'ref-3534', 1, 2, 'Memoria002', 'userref-5698', 1, null, false, null, 1, 2);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, usuario_ref, tipo_memoria_id, fecha_envio_secretaria, requiere_retrospectiva, fecha_retrospectiva, version, estado_actual_id)
 VALUES (3, 'ref-657', 1, 2, 'Memoria003', 'userref-757', 1, null, false, null, 1, 3);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, usuario_ref, tipo_memoria_id, fecha_envio_secretaria, requiere_retrospectiva, fecha_retrospectiva, version, estado_actual_id)
 VALUES (4, 'ref-4698', 1, 2, 'Memoria4', 'userref-654', 1, null, false, null, 1, 4);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, usuario_ref, tipo_memoria_id, fecha_envio_secretaria, requiere_retrospectiva, fecha_retrospectiva, version, estado_actual_id)
 VALUES (5, 'ref-4657', 1, 2, 'Memoria5', 'userref-777', 1, null, false, null, 1, 5);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, usuario_ref, tipo_memoria_id, fecha_envio_secretaria, requiere_retrospectiva, fecha_retrospectiva, version, estado_actual_id)
 VALUES (6, 'ref-6658', 1, 2, 'Memoria6', 'userref-55465', 1, null, false, null, 1, 6);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, usuario_ref, tipo_memoria_id, fecha_envio_secretaria, requiere_retrospectiva, fecha_retrospectiva, version, estado_actual_id)
 VALUES (7, 'ref-3635', 1, 2, 'Memoria7', 'userref-4444', 1, null, false, null, 1, 7);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, usuario_ref, tipo_memoria_id, fecha_envio_secretaria, requiere_retrospectiva, fecha_retrospectiva, version, estado_actual_id)
 VALUES (8, 'ref-777', 1, 2, 'Memoria8', 'userref-5555', 1, null, false, null, 1, 8);

INSERT INTO eti.estado_memoria (id, memoria_id, tipo_estado_memoria_id, fecha_estado)
 VALUES (1, 1, 1, '2020-06-05 15:00:05');
INSERT INTO eti.estado_memoria (id, memoria_id, tipo_estado_memoria_id, fecha_estado)
 VALUES (2, 2, 2, '2020-06-05 15:00:05');
INSERT INTO eti.estado_memoria (id, memoria_id, tipo_estado_memoria_id, fecha_estado)
 VALUES (3, 3, 3, '2020-06-05 15:00:05');
INSERT INTO eti.estado_memoria (id, memoria_id, tipo_estado_memoria_id, fecha_estado)
 VALUES (4, 4, 4, '2020-06-05 15:14:45');
INSERT INTO eti.estado_memoria (id, memoria_id, tipo_estado_memoria_id, fecha_estado)
 VALUES (5, 5, 5, '2020-06-05 13:14:05');
INSERT INTO eti.estado_memoria (id, memoria_id, tipo_estado_memoria_id, fecha_estado)
 VALUES (6, 6, 6, '2020-06-05 12:00:05');
INSERT INTO eti.estado_memoria (id, memoria_id, tipo_estado_memoria_id, fecha_estado)
 VALUES (7, 7, 7, '2020-06-05 18:41:05');
INSERT INTO eti.estado_memoria (id, memoria_id, tipo_estado_memoria_id, fecha_estado)
 VALUES (8, 8, 7, '2020-07-05 16:25:05');
