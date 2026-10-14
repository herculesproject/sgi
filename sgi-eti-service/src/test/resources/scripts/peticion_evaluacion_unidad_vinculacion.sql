-- DEPENDENCIAS: PETICION EVALUACION UNIDAD VINCULACION
/*
  scripts = {
  "classpath:scripts/tipo_actividad.sql",
  "classpath:scripts/peticion_evaluacion.sql",
  }
*/

-- PETICION EVALUACION UNIDAD VINCULACION
INSERT INTO test.peticion_evaluacion_unidad_vinculacion (id, peticion_evaluacion_id, unidad_vinculacion_ref) VALUES (1, 2, 'UV-1');
INSERT INTO test.peticion_evaluacion_unidad_vinculacion (id, peticion_evaluacion_id, unidad_vinculacion_ref) VALUES (2, 2, 'UV-2');
INSERT INTO test.peticion_evaluacion_unidad_vinculacion (id, peticion_evaluacion_id, unidad_vinculacion_ref) VALUES (3, 3, 'UV-1');
INSERT INTO test.peticion_evaluacion_unidad_vinculacion (id, peticion_evaluacion_id, unidad_vinculacion_ref) VALUES (4, 3, 'UV-3');

ALTER SEQUENCE test.peticion_evaluacion_unidad_vinculacion_seq RESTART WITH 5;
