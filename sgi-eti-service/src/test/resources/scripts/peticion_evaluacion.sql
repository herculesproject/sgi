-- DEPENDENCIAS: PETICION EVALUACION
/*
  scripts = { 
  "classpath:scripts/tipo_actividad.sql", 
  }
*/

--PETICION EVALUACION
INSERT INTO test.peticion_evaluacion (id, codigo, solicitud_convocatoria_ref, tipo_actividad_id, existe_financiacion, fecha_inicio, fecha_fin, resumen, valor_social, objetivos, dis_metodologico, tiene_fondos_propios, persona_ref, activo)
 VALUES(2, 'Codigo', null, 1, false, '2020-07-09T00:00:00Z', '2021-07-09T23:59:59Z', 'Resumen',  'ENSENIANZA_SUPERIOR', 'Objetivos', 'Metodologico', false, 'user', true);
INSERT INTO test.peticion_evaluacion (id, codigo, solicitud_convocatoria_ref, tipo_actividad_id, existe_financiacion, fecha_inicio, fecha_fin, resumen, valor_social, objetivos, dis_metodologico, tiene_fondos_propios, persona_ref, activo)
 VALUES(3, 'Codigo', null, 1, false, '2020-07-09T00:00:00Z', '2021-07-09T23:59:59Z', 'Resumen',  'ENSENIANZA_SUPERIOR', 'Objetivos', 'Metodologico', false, 'user', true);
INSERT INTO test.peticion_evaluacion (id, codigo, solicitud_convocatoria_ref, tipo_actividad_id, existe_financiacion, fecha_inicio, fecha_fin, resumen, valor_social, objetivos, dis_metodologico, tiene_fondos_propios, persona_ref, activo)
 VALUES(4, 'Codigo', null, 1, false, '2020-07-09T00:00:00Z', '2021-07-09T23:59:59Z', 'Resumen',  'ENSENIANZA_SUPERIOR', 'Objetivos', 'Metodologico', false, 'user', true);
INSERT INTO test.peticion_evaluacion (id, codigo, solicitud_convocatoria_ref, tipo_actividad_id, existe_financiacion, fecha_inicio, fecha_fin, resumen, valor_social, objetivos, dis_metodologico, tiene_fondos_propios, persona_ref, activo)
 VALUES(5, 'Codigo', null, 1, false, '2020-07-09T00:00:00Z', '2021-07-09T23:59:59Z', 'Resumen',  'ENSENIANZA_SUPERIOR', 'Objetivos', 'Metodologico', false, 'user', true);
INSERT INTO test.peticion_evaluacion (id, codigo, solicitud_convocatoria_ref, tipo_actividad_id, existe_financiacion, fecha_inicio, fecha_fin, resumen, valor_social, objetivos, dis_metodologico, tiene_fondos_propios, persona_ref, activo)
 VALUES(6, 'Codigo', null, 1, false, '2020-07-09T00:00:00Z', '2021-07-09T23:59:59Z', 'Resumen',  'ENSENIANZA_SUPERIOR', 'Objetivos', 'Metodologico', false, 'user', true);
INSERT INTO test.peticion_evaluacion (id, codigo, solicitud_convocatoria_ref, tipo_actividad_id, existe_financiacion, fecha_inicio, fecha_fin, resumen, valor_social, objetivos, dis_metodologico, tiene_fondos_propios, persona_ref, activo)
 VALUES(7, 'Codigo', null, 1, false, '2020-07-09T00:00:00Z', '2021-07-09T23:59:59Z', 'Resumen',  'ENSENIANZA_SUPERIOR', 'Objetivos', 'Metodologico', false, 'user', true);
INSERT INTO test.peticion_evaluacion (id, codigo, solicitud_convocatoria_ref, tipo_actividad_id, existe_financiacion, fecha_inicio, fecha_fin, resumen, valor_social, objetivos, dis_metodologico, tiene_fondos_propios, persona_ref, activo)
 VALUES(8, 'Codigo', null, 1, false, '2020-07-09T00:00:00Z', '2021-07-09T23:59:59Z', 'Resumen',  'ENSENIANZA_SUPERIOR', 'Objetivos', 'Metodologico', false, 'user', true);

--PETICION EVALUACION TITULOS
INSERT INTO test.peticion_evaluacion_titulo(peticion_evaluacion_id, lang, value_) VALUES(2, 'es', 'PeticionEvaluacion2');
INSERT INTO test.peticion_evaluacion_titulo(peticion_evaluacion_id, lang, value_) VALUES(3, 'es', 'PeticionEvaluacion3');
INSERT INTO test.peticion_evaluacion_titulo(peticion_evaluacion_id, lang, value_) VALUES(4, 'es', 'PeticionEvaluacion4');
INSERT INTO test.peticion_evaluacion_titulo(peticion_evaluacion_id, lang, value_) VALUES(5, 'es', 'PeticionEvaluacion5');
INSERT INTO test.peticion_evaluacion_titulo(peticion_evaluacion_id, lang, value_) VALUES(6, 'es', 'PeticionEvaluacion6');
INSERT INTO test.peticion_evaluacion_titulo(peticion_evaluacion_id, lang, value_) VALUES(7, 'es', 'PeticionEvaluacion7');
INSERT INTO test.peticion_evaluacion_titulo(peticion_evaluacion_id, lang, value_) VALUES(8, 'es', 'PeticionEvaluacion8');

ALTER SEQUENCE test.peticion_evaluacion_seq RESTART WITH 9;