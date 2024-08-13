-- DEPENDENCIAS: TAREA
/*
  scripts = { 
  "classpath:scripts/equipo_trabajo.sql", 
  "classpath:scripts/memoria.sql", 
  "classpath:scripts/formacion_especifica.sql", 
  "classpath:scripts/tipo_tarea.sql",  
  }
*/

-- TAREA
INSERT INTO test.tarea (id, equipo_trabajo_id, memoria_id, formacion, formacion_especifica_id, organismo, anio, tipo_tarea_id)
  VALUES (2, 2, 3, 'Formacion2', 1, 'Organismo2', 2020, 1);
INSERT INTO test.tarea (id, equipo_trabajo_id, memoria_id, formacion, formacion_especifica_id, organismo, anio, tipo_tarea_id)
  VALUES (3, 2, 2, 'Formacion3', 1, 'Organismo3', 2020, 1);
INSERT INTO test.tarea (id, equipo_trabajo_id, memoria_id, formacion, formacion_especifica_id, organismo, anio, tipo_tarea_id)
  VALUES (4, 2, 2, 'Formacion4', 1, 'Organismo4', 2020, 1);
INSERT INTO test.tarea (id, equipo_trabajo_id, memoria_id, formacion, formacion_especifica_id, organismo, anio, tipo_tarea_id)
  VALUES (5, 2, 2, 'Formacion5', 1, 'Organismo5', 2020, 1);
INSERT INTO test.tarea (id, equipo_trabajo_id, memoria_id, formacion, formacion_especifica_id, organismo, anio, tipo_tarea_id)
  VALUES (6, 2, 2, 'Formacion6', 1, 'Organismo6', 2020, 1);
INSERT INTO test.tarea (id, equipo_trabajo_id, memoria_id, formacion, formacion_especifica_id, organismo, anio, tipo_tarea_id)
  VALUES (7, 2, 2, 'Formacion7', 1, 'Organismo7', 2020, 1);
INSERT INTO test.tarea (id, equipo_trabajo_id, memoria_id, formacion, formacion_especifica_id, organismo, anio, tipo_tarea_id)
  VALUES (8, 2, 2, 'Formacion8', 1, 'Organismo8', 2020, 1);

-- TAREA NOMBRE
INSERT INTO test.tarea_nombre (tarea_id, lang, value_) VALUES (2, 'es', 'Tarea2');
INSERT INTO test.tarea_nombre (tarea_id, lang, value_) VALUES (3, 'es', 'Tarea3');
INSERT INTO test.tarea_nombre (tarea_id, lang, value_) VALUES (4, 'es', 'Tarea4');
INSERT INTO test.tarea_nombre (tarea_id, lang, value_) VALUES (5, 'es', 'Tarea5');
INSERT INTO test.tarea_nombre (tarea_id, lang, value_) VALUES (6, 'es', 'Tarea6');
INSERT INTO test.tarea_nombre (tarea_id, lang, value_) VALUES (7, 'es', 'Tarea7');
INSERT INTO test.tarea_nombre (tarea_id, lang, value_) VALUES (8, 'es', 'Tarea8');

ALTER SEQUENCE test.tarea_seq RESTART WITH 9;