-- FORMULARIO 
INSERT INTO eti.formulario (id, nombre, descripcion, activo) VALUES (1, 'M10', 'Descripcion', true);
INSERT INTO eti.formulario (id, nombre, descripcion, activo) VALUES (2, 'M20', 'Descripcion', true);
INSERT INTO eti.formulario (id, nombre, descripcion, activo) VALUES (3, 'M30', 'Descripcion', true);
INSERT INTO eti.formulario (id, nombre, descripcion, activo) VALUES (4, 'Seguimiento Anual', 'Descripcion', true);
INSERT INTO eti.formulario (id, nombre, descripcion, activo) VALUES (5, 'Seguimiento Final', 'Descripcion', true);
INSERT INTO eti.formulario (id, nombre, descripcion, activo) VALUES (6, 'Retrospectiva', 'Descripcion', true);
INSERT INTO eti.formulario (id, nombre, descripcion, activo) VALUES (7, 'Retrospectiva1', 'Descripcion', false);
INSERT INTO eti.formulario (id, nombre, descripcion, activo) VALUES (8, 'Retrospectiva2', 'Descripcion', false);

-- BLOQUE FORMULARIO
INSERT INTO eti.bloque_formulario (id, nombre, formulario_id, orden, activo) VALUES (1, 'BloqueFormulario001', 1, 8, true);
INSERT INTO eti.bloque_formulario (id, nombre, formulario_id, orden, activo) VALUES (2, 'BloqueFormulario002', 2, 7, true);
INSERT INTO eti.bloque_formulario (id, nombre, formulario_id, orden, activo) VALUES (3, 'BloqueFormulario003', 3, 6, true);
INSERT INTO eti.bloque_formulario (id, nombre, formulario_id, orden, activo) VALUES (4, 'BloqueFormulario4', 4, 5, true);
INSERT INTO eti.bloque_formulario (id, nombre, formulario_id, orden, activo) VALUES (5, 'BloqueFormulario5', 5, 4, true);
INSERT INTO eti.bloque_formulario (id, nombre, formulario_id, orden, activo) VALUES (6, 'BloqueFormulario6', 6, 3, true);
INSERT INTO eti.bloque_formulario (id, nombre, formulario_id, orden, activo) VALUES (7, 'BloqueFormulario7', 7, 2, true);
INSERT INTO eti.bloque_formulario (id, nombre, formulario_id, orden, activo) VALUES (8, 'BloqueFormulario8', 8, 1, true);