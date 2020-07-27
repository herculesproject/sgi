-- FORMULARIO
INSERT INTO ETI.FORMULARIO
(ID, NOMBRE, DESCRIPCION, ACTIVO)
VALUES(1, 'M10', 'Descripcion1', true);

-- BLOQUE FORMULARIO
INSERT INTO ETI.BLOQUE_FORMULARIO
(ID, NOMBRE, FORMULARIO_ID, ORDEN, ACTIVO)
VALUES(1, 'BloqueFormulario1', 1, 1, true);

-- COMPONENTE FORMULARIO
INSERT INTO ETI.COMPONENTE_FORMULARIO
(ID, ESQUEMA)
VALUES(1, 'EsquemaComponenteFormulario1');

-- APARTADO FORMULARIO
INSERT INTO ETI.APARTADO_FORMULARIO
(ID, BLOQUE_FORMULARIO_ID, NOMBRE, APARTADO_FORMULARIO_PADRE_ID, ORDEN, COMPONENTE_FORMULARIO_ID, ACTIVO)
VALUES(100, 1, 'ApartadoFormulario01', NULL, 1, 1, true);

-- COMITÉ
INSERT INTO eti.comite (id, comite, activo) VALUES (1, 'Comite2', true);

-- TIPO ACTIVIDAD 
INSERT INTO eti.tipo_actividad (id, nombre, activo) VALUES (1, 'TipoActividad1', true);

-- TIPO MEMORIA 
INSERT INTO eti.tipo_memoria (id, nombre, activo) VALUES (1, 'TipoMemoria1', true);

--PETICION EVALUACION
INSERT INTO eti.peticion_evaluacion (id, titulo, codigo, solicitud_convocatoria_ref, tipo_actividad_id, fuente_financiacion, fecha_inicio, fecha_fin, resumen, valor_social, otro_valor_social, objetivos, dis_metodologico, externo, tiene_fondos_propios, usuario_ref, activo)
VALUES(1, 'PeticionEvaluacion1', 'Codigo', 'Ref solicitud convocatoria', 1, 'Fuente financiadora', '2020-07-09', '2021-07-09', 'Resumen', 3, 'Otro valor social', 'Objetivos', 'Metodologico', false, false, 'user-001', true);

 -- TIPO ESTADO MEMORIA 
INSERT INTO eti.tipo_estado_memoria (id, nombre, activo) VALUES (1, 'En elaboración', true);

-- MEMORIA 
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, usuario_ref, tipo_memoria_id, fecha_envio_secretaria, requiere_retrospectiva, fecha_retrospectiva, version, estado_actual_id)
 VALUES (1, 'ref-5588', 1, 1, 'Memoria1', 'userref-55698', 1, null, false, null, 1, 1);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, usuario_ref, tipo_memoria_id, fecha_envio_secretaria, requiere_retrospectiva, fecha_retrospectiva, version, estado_actual_id)
 VALUES (2, 'ref-3534', 1, 1, 'Memoria2', 'userref-5698', 1, null, false, null, 1, 1);

 -- DICTAMEN
INSERT INTO eti.dictamen (id, nombre, activo) VALUES (1, 'Dictamen1', true);
INSERT INTO eti.dictamen (id, nombre, activo) VALUES (2, 'Dictamen2', true);

-- TIPO CONVOCATORIA REUNION 
INSERT INTO eti.tipo_convocatoria_reunion (id, nombre, activo) VALUES (1, 'Ordinaria', true);
INSERT INTO eti.tipo_convocatoria_reunion (id, nombre, activo) VALUES (2, 'Extraordinaria', true);

-- CONVOCATORIA REUNION
INSERT INTO ETI.CONVOCATORIA_REUNION
(ID, COMITE_ID, FECHA_EVALUACION, FECHA_LIMITE, LUGAR, ORDEN_DIA, CODIGO, TIPO_CONVOCATORIA_REUNION_ID, HORA_INICIO, MINUTO_INICIO, FECHA_ENVIO, ACTIVO)
VALUES(1, 1, '2020-07-01 00:00:00.000', '2020-08-01', 'Lugar 1', 'Orden del día convocatoria reunión 0', 'CR-1', 1, 8, 30, '2020-07-13', true);
INSERT INTO ETI.CONVOCATORIA_REUNION
(ID, COMITE_ID, FECHA_EVALUACION, FECHA_LIMITE, LUGAR, ORDEN_DIA, CODIGO, TIPO_CONVOCATORIA_REUNION_ID, HORA_INICIO, MINUTO_INICIO, FECHA_ENVIO, ACTIVO)
VALUES(2, 1, '2020-07-02 00:00:00.000', '2020-08-02', 'Lugar 2', 'Orden del día convocatoria reunión 2', 'CR-2', 2, 9, 30, '2020-07-13', true);

-- TIPO EVALUACION
INSERT INTO eti.tipo_evaluacion (id, nombre, activo) VALUES (1, 'TipoEvaluacion1', true);

-- EVALUACION
INSERT INTO eti.evaluacion(id, memoria_id, dictamen_id, convocatoria_reunion_id, tipo_evaluacion_id, es_rev_minima, activo) VALUES(200, 1, 1, 1, 1, true, true);

-- TIPO COMENTARIO
INSERT INTO eti.tipo_comentario (id, nombre, activo) VALUES (300, 'GESTOR', true);

-- COMENTARIO
INSERT INTO eti.comentario (id, apartado_formulario_id, evaluacion_id, tipo_comentario_id, texto)
  VALUES (2, 100, 200, 300, 'Comentario2');