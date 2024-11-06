-- TIPO DOCUMENTO
INSERT INTO test.tipo_documento (id, descripcion, activo) VALUES (1, 'descripcion-1', true);

-- TIPO_DOCUMENTO_NOMBRE
INSERT INTO test.tipo_documento_nombre (tipo_documento_id, lang, value_) VALUES (1, 'es', 'nombre-1');