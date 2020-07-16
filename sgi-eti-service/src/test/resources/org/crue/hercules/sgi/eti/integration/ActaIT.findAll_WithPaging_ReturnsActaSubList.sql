
-- COMITE
INSERT INTO eti.comite (id, comite, activo) VALUES (1, 'Comite1', true);

-- TIPO CONVOCATORIA REUNION 
INSERT INTO eti.tipo_convocatoria_reunion (id, nombre, activo) VALUES (1, 'Ordinaria', true);
INSERT INTO eti.tipo_convocatoria_reunion (id, nombre, activo) VALUES (2, 'Extraordinaria', true);
INSERT INTO eti.tipo_convocatoria_reunion (id, nombre, activo) VALUES (3, 'Seguimiento', true);

-- CONVOCATORIA REUNION
INSERT INTO ETI.CONVOCATORIA_REUNION
(ID, COMITE_ID, FECHA_EVALUACION, FECHA_LIMITE, LUGAR, ORDEN_DIA, CODIGO, TIPO_CONVOCATORIA_REUNION_ID, HORA_INICIO, MINUTO_INICIO, FECHA_ENVIO, ACTIVO)
VALUES(100, 1, '2020-07-01 00:00:00.000', '2020-08-01', 'Lugar 01', 'Orden del día convocatoria reunión 01', 'CR-01', 1, 8, 30, '2020-07-13', true);

-- ACTA
INSERT INTO eti.acta (id, convocatoria_reunion_id, hora_inicio, minuto_inicio, hora_fin, minuto_fin, resumen, numero, inactiva, activo)
  VALUES (1, 100, 10, 15, 12, 0, 'Resumen123', 123, true, true);
INSERT INTO eti.acta (id, convocatoria_reunion_id, hora_inicio, minuto_inicio, hora_fin, minuto_fin, resumen, numero, inactiva, activo)
  VALUES (2, 100, 10, 15, 12, 0, 'Resumen124', 124, true, true);
INSERT INTO eti.acta (id, convocatoria_reunion_id, hora_inicio, minuto_inicio, hora_fin, minuto_fin, resumen, numero, inactiva, activo)
  VALUES (3, 100, 10, 15, 12, 0, 'Resumen125', 125, true, true);
INSERT INTO eti.acta (id, convocatoria_reunion_id, hora_inicio, minuto_inicio, hora_fin, minuto_fin, resumen, numero, inactiva, activo)
  VALUES (4, 100, 10, 15, 12, 0, 'Resumen126', 126, true, true);
INSERT INTO eti.acta (id, convocatoria_reunion_id, hora_inicio, minuto_inicio, hora_fin, minuto_fin, resumen, numero, inactiva, activo)
  VALUES (5, 100, 10, 15, 12, 0, 'Resumen127', 127, true, true);
INSERT INTO eti.acta (id, convocatoria_reunion_id, hora_inicio, minuto_inicio, hora_fin, minuto_fin, resumen, numero, inactiva, activo)
  VALUES (6, 100, 10, 15, 12, 0, 'Resumen128', 128, true, true);
INSERT INTO eti.acta (id, convocatoria_reunion_id, hora_inicio, minuto_inicio, hora_fin, minuto_fin, resumen, numero, inactiva, activo)
  VALUES (7, 100, 10, 15, 12, 0, 'Resumen129', 129, true, true);
INSERT INTO eti.acta (id, convocatoria_reunion_id, hora_inicio, minuto_inicio, hora_fin, minuto_fin, resumen, numero, inactiva, activo)
  VALUES (8, 100, 10, 15, 12, 0, 'Resumen120', 120, true, true);
