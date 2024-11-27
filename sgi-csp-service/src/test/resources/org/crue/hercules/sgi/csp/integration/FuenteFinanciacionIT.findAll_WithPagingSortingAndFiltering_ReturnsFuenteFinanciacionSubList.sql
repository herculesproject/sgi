-- TIPO AMBITO GEOGRAFICO
INSERT INTO test.tipo_ambito_geografico (id, activo) VALUES (1, true);
INSERT INTO test.tipo_ambito_geografico (id, activo) VALUES (2, true);

INSERT INTO test.tipo_ambito_geografico_nombre (tipo_ambito_geografico_id, lang, value_) VALUES(1, 'es', 'nombre-001');
INSERT INTO test.tipo_ambito_geografico_nombre (tipo_ambito_geografico_id, lang, value_) VALUES(2, 'es', 'nombre-002');

-- TIPO ORIGEN FUENTE FINANCIACION
INSERT INTO test.tipo_origen_fuente_financiacion (id, activo) VALUES (1, true);
INSERT INTO test.tipo_origen_fuente_financiacion (id, activo) VALUES (2, true);

-- TIPO ORIGEN FUENTE FINANCIACION NOMBRE
INSERT INTO test.tipo_origen_fuente_financiacion_nombre (tipo_origen_fuente_financiacion_id, lang, value_) VALUES(1, 'es', 'nombre-001');
INSERT INTO test.tipo_origen_fuente_financiacion_nombre (tipo_origen_fuente_financiacion_id, lang, value_) VALUES(2, 'es', 'nombre-002');

-- FUENTE FINANCIACION
INSERT INTO test.fuente_financiacion (id, descripcion, fondo_estructural, tipo_ambito_geografico_id, tipo_origen_fuente_financiacion_id, activo) 
  VALUES (1, 'descripcion-001', true, 1, 1, true);
INSERT INTO test.fuente_financiacion (id, descripcion, fondo_estructural, tipo_ambito_geografico_id, tipo_origen_fuente_financiacion_id, activo) 
  VALUES (2, 'descripcion-002', true, 2, 2, true);
INSERT INTO test.fuente_financiacion (id, descripcion, fondo_estructural, tipo_ambito_geografico_id, tipo_origen_fuente_financiacion_id, activo) 
  VALUES (3, 'descripcion-003', true, 2, 2, true);
INSERT INTO test.fuente_financiacion (id, descripcion, fondo_estructural, tipo_ambito_geografico_id, tipo_origen_fuente_financiacion_id, activo) 
  VALUES (4, 'descripcion-004', true, 2, 2, false);
INSERT INTO test.fuente_financiacion (id, descripcion, fondo_estructural, tipo_ambito_geografico_id, tipo_origen_fuente_financiacion_id, activo) 
  VALUES (11, 'descripcion-011', true, 1, 1, true);
INSERT INTO test.fuente_financiacion (id, descripcion, fondo_estructural, tipo_ambito_geografico_id, tipo_origen_fuente_financiacion_id, activo) 
  VALUES (12, 'descripcion-012', true, 2, 2, true);
  INSERT INTO test.fuente_financiacion (id, descripcion, fondo_estructural, tipo_ambito_geografico_id, tipo_origen_fuente_financiacion_id, activo) 
  VALUES (13, 'descripcion-013', true, 1, 1, true);


-- FUENTE FINANCIACION NOMBRE
INSERT INTO test.fuente_financiacion_nombre (fuente_financiacion_id, lang, value_) VALUES(1, 'es', 'nombre-001');
INSERT INTO test.fuente_financiacion_nombre (fuente_financiacion_id, lang, value_) VALUES(2, 'es', 'nombre-002');
INSERT INTO test.fuente_financiacion_nombre (fuente_financiacion_id, lang, value_) VALUES(3, 'es', 'nombre-003');
INSERT INTO test.fuente_financiacion_nombre (fuente_financiacion_id, lang, value_) VALUES(4, 'es', 'nombre-004');
INSERT INTO test.fuente_financiacion_nombre (fuente_financiacion_id, lang, value_) VALUES(11, 'es', 'nombre-011');
INSERT INTO test.fuente_financiacion_nombre (fuente_financiacion_id, lang, value_) VALUES(12, 'es', 'nombre-012');
INSERT INTO test.fuente_financiacion_nombre (fuente_financiacion_id, lang, value_) VALUES(13, 'es', 'nombre-013');