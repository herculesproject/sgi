-- COMITÉ
INSERT INTO eti.comite (id, comite, activo) VALUES (1, 'Comite1', true);

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

-- FORMULARIO 
INSERT INTO eti.formulario (id, nombre, descripcion, activo) VALUES (1, 'M10', 'Descripcion', true);

-- FORMULARIO MEMORIA
INSERT INTO eti.formulario_memoria (id, memoria_id, formulario_id, activo) VALUES (1, 1, 1, true);

-- COMPONENTE FORMULARIO
INSERT INTO eti.componente_formulario (id, esquema) VALUES(1, 'EsquemaComponenteFormulario01');
