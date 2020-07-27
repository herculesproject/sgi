-- COMITÉ
INSERT INTO eti.comite (id, comite, activo) VALUES (1, 'Comite1', true);

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

-- FORMULARIO 
INSERT INTO eti.formulario (id, nombre, descripcion, activo) VALUES (1, 'M10', 'Descripcion', true);

-- FORMULARIO MEMORIA
INSERT INTO eti.formulario_memoria (id, memoria_id, formulario_id, activo) VALUES (1, 1, 1, true);

-- COMPONENTE FORMULARIO
INSERT INTO eti.componente_formulario (id, esquema) VALUES(1, 'EsquemaComponenteFormulario01');

-- RESPUESTA FORMULARIO
INSERT INTO eti.respuesta_formulario (id, formulario_memoria_id, componente_formulario_id, valor) VALUES (1, 1, 1, 'Valor001');
INSERT INTO eti.respuesta_formulario (id, formulario_memoria_id, componente_formulario_id, valor) VALUES (2, 1, 1, 'Valor002');
INSERT INTO eti.respuesta_formulario (id, formulario_memoria_id, componente_formulario_id, valor) VALUES (3, 1, 1, 'Valor003');
INSERT INTO eti.respuesta_formulario (id, formulario_memoria_id, componente_formulario_id, valor) VALUES (4, 1, 1, 'Valor4');
INSERT INTO eti.respuesta_formulario (id, formulario_memoria_id, componente_formulario_id, valor) VALUES (5, 1, 1, 'Valor5');
INSERT INTO eti.respuesta_formulario (id, formulario_memoria_id, componente_formulario_id, valor) VALUES (6, 1, 1, 'Valor6');
INSERT INTO eti.respuesta_formulario (id, formulario_memoria_id, componente_formulario_id, valor) VALUES (7, 1, 1, 'Valor7');
INSERT INTO eti.respuesta_formulario (id, formulario_memoria_id, componente_formulario_id, valor) VALUES (8, 1, 1, 'Valor8');
