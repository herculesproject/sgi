-- TIPO FINANCIACION
INSERT INTO test.tipo_financiacion (id, descripcion, activo) VALUES (1, 'descripcion-001', true);
INSERT INTO test.tipo_financiacion (id, descripcion, activo) VALUES (2, 'descripcion-002', true);
-- TIPO FINANCIACION NOMBRE
INSERT INTO test.tipo_financiacion_nombre (tipo_financiacion_id, lang, value_) VALUES(1, 'es', 'nombre-001');
INSERT INTO test.tipo_financiacion_nombre (tipo_financiacion_id, lang, value_) VALUES(2, 'es', 'nombre-002');