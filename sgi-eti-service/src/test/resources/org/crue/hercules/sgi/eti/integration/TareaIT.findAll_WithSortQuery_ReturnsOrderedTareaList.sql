-- TIPO ACTIVIDAD 
INSERT INTO eti.tipo_actividad (id, nombre, activo) VALUES (1, 'TipoActividad1', true);

--PETICION EVALUACION
INSERT INTO eti.peticion_evaluacion (id, titulo, codigo, solicitud_convocatoria_ref, tipo_actividad_id, fuente_financiacion_ref, fecha_inicio, fecha_fin, resumen, valor_social, otro_valor_social, objetivos, dis_metodologico, externo, tiene_fondos_propios, usuario_ref, activo)
VALUES(1, 'PeticionEvaluacion1', 'Codigo', 'Ref solicitud convocatoria', 1, 'Ref fuente financiadora', '2020-07-09', '2021-07-09', 'Resumen', 3, 'Otro valor social', 'Objetivos', 'Metodologico', false, false, 'user-001', true);

-- EQUIPO TRABAJO
INSERT INTO eti.equipo_trabajo (id, peticion_evaluacion_id, usuario_ref) VALUES (100, 1, 'user-1');

-- COMITÉ
INSERT INTO eti.comite (id, comite, activo) VALUES (2, 'Comite2', true);

-- TIPO MEMORIA 
INSERT INTO eti.tipo_memoria (id, nombre, activo) VALUES (1, 'TipoMemoria1', true);

-- MEMORIA 
INSERT INTO eti.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, usuario_ref,  tipo_memoria_id, fecha_envio_secretaria, requiere_retrospectiva, fecha_retrospectiva, version)
 VALUES (200, 'ref-5588', 1, 2, 'Memoria001', 'userref-55698',1, null, false, null, 1);

-- FORMACIÓN ESPECÍFICA 
INSERT INTO eti.formacion_especifica (id, nombre, activo) VALUES (300, 'FormacionEspecifica001', true);

-- TAREA
INSERT INTO eti.tarea (id, equipo_trabajo_id, memoria_id, tarea, formacion, formacion_especifica_id, organismo, anio)
  VALUES (1, 100, 200, 'Tarea001', 'Formacion1', 300, 'Organismo1', 2020);
INSERT INTO eti.tarea (id, equipo_trabajo_id, memoria_id, tarea, formacion, formacion_especifica_id, organismo, anio)
  VALUES (2, 100, 200, 'Tarea002', 'Formacion2', 300, 'Organismo2', 2020);
INSERT INTO eti.tarea (id, equipo_trabajo_id, memoria_id, tarea, formacion, formacion_especifica_id, organismo, anio)
  VALUES (3, 100, 200, 'Tarea003', 'Formacion3', 300, 'Organismo3', 2020);
INSERT INTO eti.tarea (id, equipo_trabajo_id, memoria_id, tarea, formacion, formacion_especifica_id, organismo, anio)
  VALUES (4, 100, 200, 'Tarea004', 'Formacion4', 300, 'Organismo4', 2020);
INSERT INTO eti.tarea (id, equipo_trabajo_id, memoria_id, tarea, formacion, formacion_especifica_id, organismo, anio)
  VALUES (5, 100, 200, 'Tarea005', 'Formacion5', 300, 'Organismo5', 2020);
INSERT INTO eti.tarea (id, equipo_trabajo_id, memoria_id, tarea, formacion, formacion_especifica_id, organismo, anio)
  VALUES (6, 100, 200, 'Tarea006', 'Formacion6', 300, 'Organismo6', 2020);
INSERT INTO eti.tarea (id, equipo_trabajo_id, memoria_id, tarea, formacion, formacion_especifica_id, organismo, anio)
  VALUES (7, 100, 200, 'Tarea007', 'Formacion7', 300, 'Organismo7', 2020);
INSERT INTO eti.tarea (id, equipo_trabajo_id, memoria_id, tarea, formacion, formacion_especifica_id, organismo, anio)
  VALUES (8, 100, 200, 'Tarea008', 'Formacion8', 300, 'Organismo8', 2020);