-- TIPO HITO
INSERT INTO test.tipo_hito (id, descripcion, activo) VALUES (1, 'Descripcion1', true);
INSERT INTO test.tipo_hito (id, descripcion, activo) VALUES (2, 'Descripcion2', true);

-- TIPO HITO NOMBRE
INSERT INTO test.tipo_hito_nombre (tipo_hito_id, lang, value_) VALUES (1, 'es', 'TipoHito1');
INSERT INTO test.tipo_hito_nombre (tipo_hito_id, lang, value_) VALUES (2, 'es', 'TipoHito2');
