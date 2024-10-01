-- TIPO AMBITO GEOGRAFICO
INSERT INTO test.tipo_ambito_geografico (id, activo) VALUES (1, true);
INSERT INTO test.tipo_ambito_geografico (id, activo) VALUES (2, true);

INSERT INTO test.tipo_ambito_geografico_nombre (tipo_ambito_geografico_id, lang, value_) VALUES(1, 'es', 'nombre-001');
INSERT INTO test.tipo_ambito_geografico_nombre (tipo_ambito_geografico_id, lang, value_) VALUES(2, 'es', 'nombre-002');

-- TIPO ORIGEN FUENTE FINANCIACION
INSERT INTO test.tipo_origen_fuente_financiacion (id, nombre, activo) VALUES (1, 'nombre-001', true);
INSERT INTO test.tipo_origen_fuente_financiacion (id, nombre, activo) VALUES (2, 'nombre-002', true);

-- FUENTE FINANCIACION
INSERT INTO test.fuente_financiacion (id, nombre, descripcion, fondo_estructural, tipo_ambito_geografico_id, tipo_origen_fuente_financiacion_id, activo) 
  VALUES (1, 'nombre-001', 'descripcion-001', true, 1, 1, true);
INSERT INTO test.fuente_financiacion (id, nombre, descripcion, fondo_estructural, tipo_ambito_geografico_id, tipo_origen_fuente_financiacion_id, activo) 
  VALUES (2, 'nombre-002', 'descripcion-002', true, 2, 2, true);