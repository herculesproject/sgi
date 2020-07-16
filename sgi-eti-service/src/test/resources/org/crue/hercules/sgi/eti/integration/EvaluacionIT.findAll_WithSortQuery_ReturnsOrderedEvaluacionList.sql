-- COMITÉ
INSERT INTO eti.comite (id, comite, activo) VALUES (1, 'Comite2', true);

-- TIPO ACTIVIDAD 
INSERT INTO eti.tipo_actividad (id, nombre, activo) VALUES (1, 'TipoActividad1', true);

-- TIPO MEMORIA 
INSERT INTO eti.tipo_memoria (id, nombre, activo) VALUES (1, 'TipoMemoria1', true);

--PETICION EVALUACION
INSERT INTO eti.peticion_evaluacion (id, titulo, codigo, solicitud_convocatoria_ref, tipo_actividad_id, fuente_financiacion_ref, fecha_inicio, fecha_fin, resumen, valor_social, otro_valor_social, objetivos, dis_metodologico, externo, tiene_fondos_propios, usuario_ref, activo)
VALUES(1, 'PeticionEvaluacion1', 'Codigo', 'Ref solicitud convocatoria', 1, 'Ref fuente financiadora', '2020-07-09', '2021-07-09', 'Resumen', 3, 'Otro valor social', 'Objetivos', 'Metodologico', false, false, 'user-001', true);

-- MEMORIA 
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, usuario_ref, tipo_memoria_id, fecha_envio_secretaria, requiere_retrospectiva, fecha_retrospectiva, version)
 VALUES (1, 'ref-5588', 1, 1, 'Memoria001', 'userref-55698', 1, null, false, null, 1);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, usuario_ref, tipo_memoria_id, fecha_envio_secretaria, requiere_retrospectiva, fecha_retrospectiva, version)
 VALUES (2, 'ref-3534', 1, 1, 'Memoria002', 'userref-5698', 1, null, false, null, 1);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, usuario_ref, tipo_memoria_id, fecha_envio_secretaria, requiere_retrospectiva, fecha_retrospectiva, version)
 VALUES (3, 'ref-657', 1, 1, 'Memoria003', 'userref-757', 1, null, false, null, 1);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, usuario_ref, tipo_memoria_id, fecha_envio_secretaria, requiere_retrospectiva, fecha_retrospectiva, version)
 VALUES (4, 'ref-4698', 1, 1, 'Memoria004', 'userref-654', 1, null, false, null, 1);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, usuario_ref, tipo_memoria_id, fecha_envio_secretaria, requiere_retrospectiva, fecha_retrospectiva, version)
 VALUES (5, 'ref-4657', 1, 1, 'Memoria005', 'userref-777', 1, null, false, null, 1);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, usuario_ref, tipo_memoria_id, fecha_envio_secretaria, requiere_retrospectiva, fecha_retrospectiva, version)
 VALUES (6, 'ref-6658', 1, 1, 'Memoria006', 'userref-55465', 1, null, false, null, 1);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, usuario_ref, tipo_memoria_id, fecha_envio_secretaria, requiere_retrospectiva, fecha_retrospectiva, version)
 VALUES (7, 'ref-3635', 1, 1, 'Memoria007', 'userref-4444', 1, null, false, null, 1);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, usuario_ref, tipo_memoria_id, fecha_envio_secretaria, requiere_retrospectiva, fecha_retrospectiva, version)
 VALUES (8, 'ref-777', 1, 1, 'Memoria008', 'userref-5555', 1, null, false, null, 1);

-- DICTAMEN
INSERT INTO eti.dictamen (id, nombre, activo) VALUES (1, 'Dictamen001', true);
INSERT INTO eti.dictamen (id, nombre, activo) VALUES (2, 'Dictamen002', true);
INSERT INTO eti.dictamen (id, nombre, activo) VALUES (3, 'Dictamen003', true);
INSERT INTO eti.dictamen (id, nombre, activo) VALUES (4, 'Dictamen004', true);
INSERT INTO eti.dictamen (id, nombre, activo) VALUES (5, 'Dictamen005', true);
INSERT INTO eti.dictamen (id, nombre, activo) VALUES (6, 'Dictamen006', true);
INSERT INTO eti.dictamen (id, nombre, activo) VALUES (7, 'Dictamen007', true);
INSERT INTO eti.dictamen (id, nombre, activo) VALUES (8, 'Dictamen008', true);

-- TIPO CONVOCATORIA REUNION 
INSERT INTO eti.tipo_convocatoria_reunion (id, nombre, activo) VALUES (1, 'Ordinaria', true);
INSERT INTO eti.tipo_convocatoria_reunion (id, nombre, activo) VALUES (2, 'Extraordinaria', true);
INSERT INTO eti.tipo_convocatoria_reunion (id, nombre, activo) VALUES (3, 'Seguimiento', true);

-- CONVOCATORIA REUNION
INSERT INTO ETI.CONVOCATORIA_REUNION
(ID, COMITE_ID, FECHA_EVALUACION, FECHA_LIMITE, LUGAR, ORDEN_DIA, CODIGO, TIPO_CONVOCATORIA_REUNION_ID, HORA_INICIO, MINUTO_INICIO, FECHA_ENVIO, ACTIVO)
VALUES(1, 1, '2020-07-01 00:00:00.000', '2020-08-01', 'Lugar 1', 'Orden del día convocatoria reunión 1', 'CR-001', 1, 8, 30, '2020-07-13', true);
INSERT INTO ETI.CONVOCATORIA_REUNION
(ID, COMITE_ID, FECHA_EVALUACION, FECHA_LIMITE, LUGAR, ORDEN_DIA, CODIGO, TIPO_CONVOCATORIA_REUNION_ID, HORA_INICIO, MINUTO_INICIO, FECHA_ENVIO, ACTIVO)
VALUES(2, 1, '2020-07-02 00:00:00.000', '2020-08-02', 'Lugar 2', 'Orden del día convocatoria reunión 2', 'CR-002', 2, 9, 30, '2020-07-13', true);
INSERT INTO ETI.CONVOCATORIA_REUNION
(ID, COMITE_ID, FECHA_EVALUACION, FECHA_LIMITE, LUGAR, ORDEN_DIA, CODIGO, TIPO_CONVOCATORIA_REUNION_ID, HORA_INICIO, MINUTO_INICIO, FECHA_ENVIO, ACTIVO)
VALUES(3, 1, '2020-07-03 00:00:00.000', '2020-08-03', 'Lugar 3', 'Orden del día convocatoria reunión 3', 'CR-003', 1, 10, 30, '2020-07-13', true);
INSERT INTO ETI.CONVOCATORIA_REUNION
(ID, COMITE_ID, FECHA_EVALUACION, FECHA_LIMITE, LUGAR, ORDEN_DIA, CODIGO, TIPO_CONVOCATORIA_REUNION_ID, HORA_INICIO, MINUTO_INICIO, FECHA_ENVIO, ACTIVO)
VALUES(4, 1, '2020-07-04 00:00:00.000', '2020-08-04', 'Lugar 4', 'Orden del día convocatoria reunión 4', 'CR-004', 2, 11, 30, '2020-07-13', true);
INSERT INTO ETI.CONVOCATORIA_REUNION
(ID, COMITE_ID, FECHA_EVALUACION, FECHA_LIMITE, LUGAR, ORDEN_DIA, CODIGO, TIPO_CONVOCATORIA_REUNION_ID, HORA_INICIO, MINUTO_INICIO, FECHA_ENVIO, ACTIVO)
VALUES(5, 1, '2020-07-05 00:00:00.000', '2020-08-05', 'Lugar 5', 'Orden del día convocatoria reunión 5', 'CR-005', 1, 12, 30, '2020-07-13', true);
INSERT INTO ETI.CONVOCATORIA_REUNION
(ID, COMITE_ID, FECHA_EVALUACION, FECHA_LIMITE, LUGAR, ORDEN_DIA, CODIGO, TIPO_CONVOCATORIA_REUNION_ID, HORA_INICIO, MINUTO_INICIO, FECHA_ENVIO, ACTIVO)
VALUES(6, 1, '2020-07-05 00:00:00.000', '2020-08-05', 'Lugar 5', 'Orden del día convocatoria reunión 6', 'CR-006', 3, 12, 30, '2020-07-13', true);
INSERT INTO ETI.CONVOCATORIA_REUNION
(ID, COMITE_ID, FECHA_EVALUACION, FECHA_LIMITE, LUGAR, ORDEN_DIA, CODIGO, TIPO_CONVOCATORIA_REUNION_ID, HORA_INICIO, MINUTO_INICIO, FECHA_ENVIO, ACTIVO)
VALUES(7, 1, '2020-07-05 00:00:00.000', '2020-08-05', 'Lugar 5', 'Orden del día convocatoria reunión 7', 'CR-007', 1, 12, 30, '2020-07-13', true);
INSERT INTO ETI.CONVOCATORIA_REUNION
(ID, COMITE_ID, FECHA_EVALUACION, FECHA_LIMITE, LUGAR, ORDEN_DIA, CODIGO, TIPO_CONVOCATORIA_REUNION_ID, HORA_INICIO, MINUTO_INICIO, FECHA_ENVIO, ACTIVO)
VALUES(8, 1, '2020-07-05 00:00:00.000', '2020-08-05', 'Lugar 5', 'Orden del día convocatoria reunión 8', 'CR-008', 3, 12, 30, '2020-07-13', true);


-- EVALUACION
INSERT INTO eti.evaluacion(id, memoria_id, dictamen_id, convocatoria_reunion_id, es_rev_minima, activo) VALUES(1, 1, 1, 1, true, true);
INSERT INTO eti.evaluacion(id, memoria_id, dictamen_id, convocatoria_reunion_id, es_rev_minima, activo) VALUES(2, 2, 2, 2, true, true);
INSERT INTO eti.evaluacion(id, memoria_id, dictamen_id, convocatoria_reunion_id, es_rev_minima, activo) VALUES(3, 3, 3, 3, true, true);
INSERT INTO eti.evaluacion(id, memoria_id, dictamen_id, convocatoria_reunion_id, es_rev_minima, activo) VALUES(4, 4, 4, 4, true, true);
INSERT INTO eti.evaluacion(id, memoria_id, dictamen_id, convocatoria_reunion_id, es_rev_minima, activo) VALUES(5, 5, 5, 5, true, true);
INSERT INTO eti.evaluacion(id, memoria_id, dictamen_id, convocatoria_reunion_id, es_rev_minima, activo) VALUES(6, 6, 6, 6, true, true);
INSERT INTO eti.evaluacion(id, memoria_id, dictamen_id, convocatoria_reunion_id, es_rev_minima, activo) VALUES(7, 7, 7, 7, true, true);
INSERT INTO eti.evaluacion(id, memoria_id, dictamen_id, convocatoria_reunion_id, es_rev_minima, activo) VALUES(8, 8, 8, 8, true, true);
