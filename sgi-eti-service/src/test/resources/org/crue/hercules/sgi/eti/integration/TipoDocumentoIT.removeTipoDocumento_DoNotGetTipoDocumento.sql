-- FORMULARIO 
INSERT INTO eti.formulario (id, nombre, descripcion, activo) VALUES (1, 'M10', 'Formulario M10', true);

-- TIPO DOCUMENTO 
INSERT INTO eti.tipo_documento (id, nombre, formulario_id, activo) VALUES (2, 'TipoDocumento2', 1, true);
