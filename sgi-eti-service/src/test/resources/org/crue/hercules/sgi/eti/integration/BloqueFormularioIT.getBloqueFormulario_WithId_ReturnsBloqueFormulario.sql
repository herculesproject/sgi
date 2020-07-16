-- FORMULARIO 
INSERT INTO eti.formulario (id, nombre, descripcion, activo) VALUES (1, 'M10', 'Descripcion', true);
INSERT INTO eti.formulario (id, nombre, descripcion, activo) VALUES (2, 'M20', 'Descripcion', true);

-- BLOQUE FORMULARIO
INSERT INTO eti.bloque_formulario (id, nombre, formulario_id, orden, activo) VALUES (1, 'BloqueFormulario1', 1, 8, true);
INSERT INTO eti.bloque_formulario (id, nombre, formulario_id, orden, activo) VALUES (2, 'BloqueFormulario2', 2, 7, true);