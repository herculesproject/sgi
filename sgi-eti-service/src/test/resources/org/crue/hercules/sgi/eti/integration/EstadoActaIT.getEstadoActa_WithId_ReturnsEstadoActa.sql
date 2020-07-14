-- ACTA 
INSERT INTO eti.acta (id) VALUES (100);

-- TIPO ESTADO ACTA
INSERT INTO eti.tipo_estado_acta (id, nombre, activo) VALUES (200, 'tipo1', true);

-- ESTADO ACTA
INSERT INTO eti.estado_acta (id, acta_id, tipo_estado_acta_id, fecha_estado)
  VALUES (1, 100, 200, '2020-07-14 19:30:00');
