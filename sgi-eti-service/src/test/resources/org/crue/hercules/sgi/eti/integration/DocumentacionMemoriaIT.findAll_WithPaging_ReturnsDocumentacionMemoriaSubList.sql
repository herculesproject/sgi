-- COMITÉ
INSERT INTO eti.comite (id, comite, activo) VALUES (1, 'Comite1', true);

-- FORMULARIO 
INSERT INTO eti.formulario (id, nombre, descripcion, activo) VALUES (1, 'M10', 'Formulario M10', true);

-- TIPO ACTIVIDAD 
INSERT INTO eti.tipo_actividad (id, nombre, activo) VALUES (1, 'TipoActividad1', true);

-- TIPO MEMORIA 
INSERT INTO eti.tipo_memoria (id, nombre, activo) VALUES (1, 'TipoMemoria1', true);

--PETICION EVALUACION
INSERT INTO eti.peticion_evaluacion (id, titulo, codigo, solicitud_convocatoria_ref, tipo_actividad_id, fuente_financiacion_ref, fecha_inicio, fecha_fin, resumen, valor_social, otro_valor_social, objetivos, dis_metodologico, externo, tiene_fondos_propios, usuario_ref, activo)
VALUES(1, 'PeticionEvaluacion1', 'Codigo', 'Ref solicitud convocatoria', 1, 'Ref fuente financiadora', '2020-07-09', '2021-07-09', 'Resumen', 3, 'Otro valor social', 'Objetivos', 'Metodologico', false, false, 'user-001', true);
INSERT INTO eti.peticion_evaluacion (id, titulo, codigo, solicitud_convocatoria_ref, tipo_actividad_id, fuente_financiacion_ref, fecha_inicio, fecha_fin, resumen, valor_social, otro_valor_social, objetivos, dis_metodologico, externo, tiene_fondos_propios, usuario_ref, activo)
VALUES(2, 'PeticionEvaluacion2', 'Codigo', 'Ref solicitud convocatoria', 1, 'Ref fuente financiadora', '2020-07-09', '2021-07-09', 'Resumen', 3, 'Otro valor social', 'Objetivos', 'Metodologico', false, false, 'user-001', true);
INSERT INTO eti.peticion_evaluacion (id, titulo, codigo, solicitud_convocatoria_ref, tipo_actividad_id, fuente_financiacion_ref, fecha_inicio, fecha_fin, resumen, valor_social, otro_valor_social, objetivos, dis_metodologico, externo, tiene_fondos_propios, usuario_ref, activo)
VALUES(3, 'PeticionEvaluacion3', 'Codigo', 'Ref solicitud convocatoria', 1, 'Ref fuente financiadora', '2020-07-09', '2021-07-09', 'Resumen', 3, 'Otro valor social', 'Objetivos', 'Metodologico', false, false, 'user-001', true);
INSERT INTO eti.peticion_evaluacion (id, titulo, codigo, solicitud_convocatoria_ref, tipo_actividad_id, fuente_financiacion_ref, fecha_inicio, fecha_fin, resumen, valor_social, otro_valor_social, objetivos, dis_metodologico, externo, tiene_fondos_propios, usuario_ref, activo)
VALUES(4, 'PeticionEvaluacion4', 'Codigo', 'Ref solicitud convocatoria', 1, 'Ref fuente financiadora', '2020-07-09', '2021-07-09', 'Resumen', 3, 'Otro valor social', 'Objetivos', 'Metodologico', false, false, 'user-001', true);
INSERT INTO eti.peticion_evaluacion (id, titulo, codigo, solicitud_convocatoria_ref, tipo_actividad_id, fuente_financiacion_ref, fecha_inicio, fecha_fin, resumen, valor_social, otro_valor_social, objetivos, dis_metodologico, externo, tiene_fondos_propios, usuario_ref, activo)
VALUES(5, 'PeticionEvaluacion5', 'Codigo', 'Ref solicitud convocatoria', 1, 'Ref fuente financiadora', '2020-07-09', '2021-07-09', 'Resumen', 3, 'Otro valor social', 'Objetivos', 'Metodologico', false, false, 'user-001', true);
INSERT INTO eti.peticion_evaluacion (id, titulo, codigo, solicitud_convocatoria_ref, tipo_actividad_id, fuente_financiacion_ref, fecha_inicio, fecha_fin, resumen, valor_social, otro_valor_social, objetivos, dis_metodologico, externo, tiene_fondos_propios, usuario_ref, activo)
VALUES(6, 'PeticionEvaluacion6', 'Codigo', 'Ref solicitud convocatoria', 1, 'Ref fuente financiadora', '2020-07-09', '2021-07-09', 'Resumen', 3, 'Otro valor social', 'Objetivos', 'Metodologico', false, false, 'user-001', true);
INSERT INTO eti.peticion_evaluacion (id, titulo, codigo, solicitud_convocatoria_ref, tipo_actividad_id, fuente_financiacion_ref, fecha_inicio, fecha_fin, resumen, valor_social, otro_valor_social, objetivos, dis_metodologico, externo, tiene_fondos_propios, usuario_ref, activo)
VALUES(7, 'PeticionEvaluacion7', 'Codigo', 'Ref solicitud convocatoria', 1, 'Ref fuente financiadora', '2020-07-09', '2021-07-09', 'Resumen', 3, 'Otro valor social', 'Objetivos', 'Metodologico', false, false, 'user-001', true);
INSERT INTO eti.peticion_evaluacion (id, titulo, codigo, solicitud_convocatoria_ref, tipo_actividad_id, fuente_financiacion_ref, fecha_inicio, fecha_fin, resumen, valor_social, otro_valor_social, objetivos, dis_metodologico, externo, tiene_fondos_propios, usuario_ref, activo)
VALUES(8, 'PeticionEvaluacion8', 'Codigo', 'Ref solicitud convocatoria', 1, 'Ref fuente financiadora', '2020-07-09', '2021-07-09', 'Resumen', 3, 'Otro valor social', 'Objetivos', 'Metodologico', false, false, 'user-001', true);

 -- TIPO ESTADO MEMORIA 
INSERT INTO eti.tipo_estado_memoria (id, nombre, activo) VALUES (1, 'En elaboración', true);

-- MEMORIA 
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, usuario_ref, tipo_memoria_id, fecha_envio_secretaria, requiere_retrospectiva, fecha_retrospectiva, version, estado_actual_id)
 VALUES (1, 'ref-5588', 1, 1, 'Memoria1', 'userref-55698', 1, null, false, null, 1, 1);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, usuario_ref, tipo_memoria_id, fecha_envio_secretaria, requiere_retrospectiva, fecha_retrospectiva, version, estado_actual_id)
 VALUES (2, 'ref-5588', 2, 1, 'Memoria2', 'userref-55698', 1, null, false, null, 1, 1);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, usuario_ref, tipo_memoria_id, fecha_envio_secretaria, requiere_retrospectiva, fecha_retrospectiva, version, estado_actual_id)
 VALUES (3, 'ref-5588', 3, 1, 'Memoria3', 'userref-55698', 1, null, false, null, 1, 1);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, usuario_ref, tipo_memoria_id, fecha_envio_secretaria, requiere_retrospectiva, fecha_retrospectiva, version, estado_actual_id)
 VALUES (4, 'ref-5588', 4, 1, 'Memoria4', 'userref-55698', 1, null, false, null, 1, 1);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, usuario_ref, tipo_memoria_id, fecha_envio_secretaria, requiere_retrospectiva, fecha_retrospectiva, version, estado_actual_id)
 VALUES (5, 'ref-5588', 5, 1, 'Memoria5', 'userref-55698', 1, null, false, null, 1, 1);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, usuario_ref, tipo_memoria_id, fecha_envio_secretaria, requiere_retrospectiva, fecha_retrospectiva, version, estado_actual_id)
 VALUES (6, 'ref-5588', 6, 1, 'Memoria6', 'userref-55698', 1, null, false, null, 1, 1);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, usuario_ref, tipo_memoria_id, fecha_envio_secretaria, requiere_retrospectiva, fecha_retrospectiva, version, estado_actual_id)
 VALUES (7, 'ref-5588', 7, 1, 'Memoria7', 'userref-55698', 1, null, false, null, 1, 1);
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, usuario_ref, tipo_memoria_id, fecha_envio_secretaria, requiere_retrospectiva, fecha_retrospectiva, version, estado_actual_id)
 VALUES (8, 'ref-5588', 8, 1, 'Memoria8', 'userref-55698', 1, null, false, null, 1, 1);

 -- TIPO DOCUMENTO 
INSERT INTO eti.tipo_documento (id, nombre, formulario_id, activo) VALUES (1, 'TipoDocumento1', 1, true);

-- DOCUMENTACIÓN MEMORIA
INSERT INTO eti.documentacion_memoria (id, memoria_id, tipo_documento_id, documento_ref) VALUES (1, 1, 1, 'doc-001');
INSERT INTO eti.documentacion_memoria (id, memoria_id, tipo_documento_id, documento_ref) VALUES (2, 2, 1, 'doc-002');
INSERT INTO eti.documentacion_memoria (id, memoria_id, tipo_documento_id, documento_ref) VALUES (3, 3, 1, 'doc-003');
INSERT INTO eti.documentacion_memoria (id, memoria_id, tipo_documento_id, documento_ref) VALUES (4, 4, 1, 'doc-004');
INSERT INTO eti.documentacion_memoria (id, memoria_id, tipo_documento_id, documento_ref) VALUES (5, 5, 1, 'doc-005');
INSERT INTO eti.documentacion_memoria (id, memoria_id, tipo_documento_id, documento_ref) VALUES (6, 6, 1, 'doc-006');
INSERT INTO eti.documentacion_memoria (id, memoria_id, tipo_documento_id, documento_ref) VALUES (7, 7, 1, 'doc-007');
INSERT INTO eti.documentacion_memoria (id, memoria_id, tipo_documento_id, documento_ref) VALUES (8, 8, 1, 'doc-008');
