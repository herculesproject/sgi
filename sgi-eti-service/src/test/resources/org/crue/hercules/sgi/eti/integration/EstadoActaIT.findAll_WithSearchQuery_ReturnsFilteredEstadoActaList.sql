-- ACTA 
INSERT INTO eti.acta (id) VALUES (100);

-- TIPO ESTADO ACTA
INSERT INTO eti.tipo_estado_acta (id, nombre, activo) VALUES (200, 'tipo1', true);

-- ESTADO ACTA
INSERT INTO eti.estado_acta (id, acta_id, tipo_estado_acta_id, fecha_estado)
  VALUES (1, 100, 200, '2020-07-14 19:30:00');
INSERT INTO eti.estado_acta (id, acta_id, tipo_estado_acta_id, fecha_estado)
  VALUES (2, 100, 200, '2020-07-14 19:32:00');
INSERT INTO eti.estado_acta (id, acta_id, tipo_estado_acta_id, fecha_estado)
  VALUES (3, 100, 200, '2020-07-14 19:33:00');
INSERT INTO eti.estado_acta (id, acta_id, tipo_estado_acta_id, fecha_estado)
  VALUES (4, 100, 200, '2020-07-14 19:34:00');
INSERT INTO eti.estado_acta (id, acta_id, tipo_estado_acta_id, fecha_estado)
  VALUES (5, 100, 200, '2020-07-14 19:35:00');
INSERT INTO eti.estado_acta (id, acta_id, tipo_estado_acta_id, fecha_estado)
  VALUES (6, 100, 200, '2020-07-14 19:36:00');
INSERT INTO eti.estado_acta (id, acta_id, tipo_estado_acta_id, fecha_estado)
  VALUES (7, 100, 200, '2020-07-14 19:37:00');
INSERT INTO eti.estado_acta (id, acta_id, tipo_estado_acta_id, fecha_estado)
  VALUES (8, 100, 200, '2020-07-14 19:38:00');