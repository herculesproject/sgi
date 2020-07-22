-- COMITÉ
INSERT INTO eti.comite (id, comite, activo) VALUES (2, 'Comite2', true);

-- TIPO ACTIVIDAD 
INSERT INTO eti.tipo_actividad (id, nombre, activo) VALUES (2, 'TipoActividad2', true);

-- TIPO MEMORIA 
INSERT INTO eti.tipo_memoria (id, nombre, activo) VALUES (2, 'TipoMemoria2', true);

--PETICION EVALUACION
INSERT INTO eti.peticion_evaluacion (id, titulo, codigo, solicitud_convocatoria_ref, tipo_actividad_id, fuente_financiacion_ref, fecha_inicio, fecha_fin, resumen, valor_social, otro_valor_social, objetivos, dis_metodologico, externo, tiene_fondos_propios, usuario_ref, activo)
VALUES(2, 'PeticionEvaluacion1', 'Codigo', 'Ref solicitud convocatoria', 2, 'Ref fuente financiadora', '2020-07-09', '2021-07-09', 'Resumen', 3, 'Otro valor social', 'Objetivos', 'Metodologico', false, false, 'user-001', true);

 -- TIPO ESTADO MEMORIA 
INSERT INTO eti.tipo_estado_memoria (id, nombre, activo) VALUES (1, 'En elaboración', true);

-- MEMORIA 
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, usuario_ref, tipo_memoria_id, fecha_envio_secretaria, requiere_retrospectiva, fecha_retrospectiva, version, estado_actual_id)
 VALUES (2, 'ref-5588', 2, 2, 'Memoria1', 'userref-55698', 2, null, false, null, 1, 1);

 -- DICTAMEN
INSERT INTO eti.dictamen (id, nombre, activo) VALUES (2, 'Dictamen1', true);

-- TIPO CONVOCATORIA REUNION 
INSERT INTO eti.tipo_convocatoria_reunion (id, nombre, activo) VALUES (2, 'Ordinaria', true);

-- CONVOCATORIA REUNION
INSERT INTO ETI.CONVOCATORIA_REUNION
(ID, COMITE_ID, FECHA_EVALUACION, FECHA_LIMITE, LUGAR, ORDEN_DIA, CODIGO, TIPO_CONVOCATORIA_REUNION_ID, HORA_INICIO, MINUTO_INICIO, FECHA_ENVIO, ACTIVO)
VALUES(2, 2, '2020-07-01 00:00:00.000', '2020-08-01', 'Lugar 1', 'Orden del día convocatoria reunión 0', 'CR-1', 2, 8, 30, '2020-07-13', true);

-- EVALUACION
INSERT INTO eti.evaluacion(id, memoria_id, dictamen_id, convocatoria_reunion_id, es_rev_minima, activo) VALUES(2, 2, 2, 2, true, true);