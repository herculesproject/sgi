-- COMITÉ
INSERT INTO eti.comite (id, comite, activo) VALUES (2, 'Comite2', true);

-- TIPO ACTIVIDAD 
INSERT INTO eti.tipo_actividad (id, nombre, activo) VALUES (1, 'TipoActividad1', true);

-- TIPO MEMORIA 
INSERT INTO eti.tipo_memoria (id, nombre, activo) VALUES (1, 'TipoMemoria1', true);

--PETICION EVALUACION
INSERT INTO eti.peticion_evaluacion (id, titulo, codigo, solicitud_convocatoria_ref, tipo_actividad_id, fuente_financiacion_ref, fecha_inicio, fecha_fin, resumen, valor_social, otro_valor_social, objetivos, dis_metodologico, externo, tiene_fondos_propios, usuario_ref, activo)
VALUES(1, 'PeticionEvaluacion1', 'Codigo', 'Ref solicitud convocatoria', 1, 'Ref fuente financiadora', '2020-07-09', '2021-07-09', 'Resumen', 3, 'Otro valor social', 'Objetivos', 'Metodologico', false, false, 'user-001', true);

-- MEMORIA 
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, usuario_ref, fecha_estado, tipo_memoria_id, fecha_envio_secretaria, requiere_retrospectiva, fecha_retrospectiva, version)
 VALUES (1, 'ref-5588', 1, 2, 'Memoria001', 'userref-55698', '2020-06-05',1, null, false, null, 1);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, usuario_ref, fecha_estado, tipo_memoria_id, fecha_envio_secretaria, requiere_retrospectiva, fecha_retrospectiva, version)
 VALUES (2, 'ref-3534', 1, 2, 'Memoria002', 'userref-5698', '2020-07-05',1, null, false, null, 1);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, usuario_ref, fecha_estado, tipo_memoria_id, fecha_envio_secretaria, requiere_retrospectiva, fecha_retrospectiva, version)
 VALUES (3, 'ref-657', 1, 2, 'Memoria003', 'userref-757', '2020-06-10',1, null, false, null, 1);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, usuario_ref, fecha_estado, tipo_memoria_id, fecha_envio_secretaria, requiere_retrospectiva, fecha_retrospectiva, version)
 VALUES (4, 'ref-4698', 1, 2, 'Memoria4', 'userref-654', '2020-02-05',1, null, false, null, 1);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, usuario_ref, fecha_estado, tipo_memoria_id, fecha_envio_secretaria, requiere_retrospectiva, fecha_retrospectiva, version)
 VALUES (5, 'ref-4657', 1, 2, 'Memoria5', 'userref-777', '2020-06-15',1, null, false, null, 1);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, usuario_ref, fecha_estado, tipo_memoria_id, fecha_envio_secretaria, requiere_retrospectiva, fecha_retrospectiva, version)
 VALUES (6, 'ref-6658', 1, 2, 'Memoria6', 'userref-55465', '2020-05-05',1, null, false, null, 1);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, usuario_ref, fecha_estado, tipo_memoria_id, fecha_envio_secretaria, requiere_retrospectiva, fecha_retrospectiva, version)
 VALUES (7, 'ref-3635', 1, 2, 'Memoria7', 'userref-4444', '2020-05-14',1, null, false, null, 1);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, usuario_ref, fecha_estado, tipo_memoria_id, fecha_envio_secretaria, requiere_retrospectiva, fecha_retrospectiva, version)
 VALUES (8, 'ref-777', 1, 2, 'Memoria8', 'userref-5555', '2020-06-05',1, null, false, null, 1);

  -- INFORME FORMULARIO 
INSERT INTO eti.informe_formulario (id, memoria_id, documento_ref, version)
 VALUES (1, 1, 'DocumentoFormulario001', 1);
 INSERT INTO eti.informe_formulario (id, memoria_id, documento_ref, version)
 VALUES (2, 2, 'DocumentoFormulario002', 2);
 INSERT INTO eti.informe_formulario (id, memoria_id, documento_ref, version)
 VALUES (3, 3, 'DocumentoFormulario003', 3);
 INSERT INTO eti.informe_formulario (id, memoria_id, documento_ref, version)
 VALUES (4, 4, 'DocumentoFormulario4', 4);
 INSERT INTO eti.informe_formulario (id, memoria_id, documento_ref, version)
 VALUES (5, 5, 'DocumentoFormulario5', 5);
 INSERT INTO eti.informe_formulario (id, memoria_id, documento_ref, version)
 VALUES (6, 6, 'DocumentoFormulario6', 6);
 INSERT INTO eti.informe_formulario (id, memoria_id, documento_ref, version)
 VALUES (7, 7, 'DocumentoFormulario7', 7);
 INSERT INTO eti.informe_formulario (id, memoria_id, documento_ref, version)
 VALUES (8, 8, 'DocumentoFormulario8', 8);
