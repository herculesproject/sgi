-- COMITE
INSERT INTO eti.comite (id, comite, activo) VALUES (1, 'Comite1', true);

-- CARGO COMITE
INSERT INTO eti.cargo_comite (id, nombre, activo) VALUES (1, 'CargoComite1', true);

-- TIPO ACTIVIDAD 
INSERT INTO eti.tipo_actividad (id, nombre, activo) VALUES (1, 'TipoActividad1', true);

-- TIPO MEMORIA 
INSERT INTO eti.tipo_memoria (id, nombre, activo) VALUES (1, 'TipoMemoria1', true);

--PETICION EVALUACION
INSERT INTO eti.peticion_evaluacion (id, titulo, codigo, solicitud_convocatoria_ref, tipo_actividad_id, fuente_financiacion_ref, fecha_inicio, fecha_fin, resumen, valor_social, otro_valor_social, objetivos, dis_metodologico, externo, tiene_fondos_propios, usuario_ref, activo)
VALUES(1, 'PeticionEvaluacion1', 'Codigo', 'Ref solicitud convocatoria', 1, 'Ref fuente financiadora', '2020-07-09', '2021-07-09', 'Resumen', 3, 'Otro valor social', 'Objetivos', 'Metodologico', false, false, 'user-001', true);

-- MEMORIA 
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, usuario_ref, tipo_memoria_id, fecha_envio_secretaria, requiere_retrospectiva, fecha_retrospectiva, version)
 VALUES (1, 'ref-5588', 1, 1, 'Memoria1', 'userref-55698', 1, null, false, null, 1);

-- DICTAMEN
INSERT INTO eti.dictamen (id, nombre, activo) VALUES (1, 'Dictamen1', true);

-- TIPO CONVOCATORIA REUNION 
INSERT INTO eti.tipo_convocatoria_reunion (id, nombre, activo) VALUES (1, 'Ordinaria', true);

-- CONVOCATORIA REUNION
INSERT INTO ETI.CONVOCATORIA_REUNION
(ID, COMITE_ID, FECHA_EVALUACION, FECHA_LIMITE, LUGAR, ORDEN_DIA, CODIGO, TIPO_CONVOCATORIA_REUNION_ID, HORA_INICIO, MINUTO_INICIO, FECHA_ENVIO, ACTIVO)
VALUES(1, 1, '2020-07-01 00:00:00.000', '2020-08-01', 'Lugar 1', 'Orden del día convocatoria reunión 0', 'CR-1', 1, 8, 30, '2020-07-13', true);

-- EVALUADOR
INSERT INTO eti.evaluador (id, resumen, comite_id, cargo_comite_id, fecha_alta, fecha_baja, usuario_ref, activo)
VALUES (1, 'Evaluador1', 1, 1, '2020-07-01', '2021-07-01', 'user-001', true);

-- EVALUACION
INSERT INTO eti.evaluacion(id, memoria_id, dictamen_id, convocatoria_reunion_id, es_rev_minima, fecha_dictamen, version, activo) VALUES(1, 1, 1, 1, true, '2020-07-10', 1, true);
