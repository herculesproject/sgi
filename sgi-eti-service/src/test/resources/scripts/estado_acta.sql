-- DEPENDENCIAS: ESTADO ACTA
/*
  scripts = { 
    "classpath:scripts/formulario.sql",
    "classpath:scripts/tipo_convocatoria_reunion.sql",
    "classpath:scripts/tipo_estado_acta.sql"
  }
*/

-- COMITE
INSERT INTO eti.comite (id, comite, formulario_id, activo) VALUES (1, 'Comite1', 1, true);

-- CONVOCATORIA REUNION
INSERT INTO eti.convocatoria_reunion
(id, comite_id, fecha_evaluacion, fecha_limite, lugar, orden_dia, anio, numero_acta, tipo_convocatoria_reunion_id, hora_inicio, minuto_inicio, fecha_envio, activo)
VALUES(1, 1, '2020-07-01T00:00:00Z', '2020-08-01T23:59:59Z', 'Lugar 01', 'Orden del día convocatoria reunión 01', 2020, 1, 1, 8, 30, '2020-07-13T00:00:00Z', true);

-- ACTA
INSERT INTO eti.acta (id, convocatoria_reunion_id, hora_inicio, minuto_inicio, hora_fin, minuto_fin, resumen, numero, estado_actual_id, inactiva, activo)
  VALUES (1, 1, 10, 15, 12, 0, 'Resumen123', 123, 1, true, true);

-- ESTADO ACTA
INSERT INTO eti.estado_acta (id, acta_id, tipo_estado_acta_id, fecha_estado)
  VALUES (4, 1, 1, '2020-07-14T00:00:00Z');
INSERT INTO eti.estado_acta (id, acta_id, tipo_estado_acta_id, fecha_estado)
  VALUES (5, 1, 1, '2020-07-14T00:00:00Z');
INSERT INTO eti.estado_acta (id, acta_id, tipo_estado_acta_id, fecha_estado)
  VALUES (6, 1, 1, '2020-07-14T00:00:00Z');
INSERT INTO eti.estado_acta (id, acta_id, tipo_estado_acta_id, fecha_estado)
  VALUES (7, 1, 1, '2020-07-14T00:00:00Z');
INSERT INTO eti.estado_acta (id, acta_id, tipo_estado_acta_id, fecha_estado)
  VALUES (8, 1, 1, '2020-07-14T00:00:00Z');
INSERT INTO eti.estado_acta (id, acta_id, tipo_estado_acta_id, fecha_estado)
  VALUES (9, 1, 1, '2020-07-14T00:00:00Z');
INSERT INTO eti.estado_acta (id, acta_id, tipo_estado_acta_id, fecha_estado)
  VALUES (10, 1, 1, '2020-07-14T00:00:00Z');
