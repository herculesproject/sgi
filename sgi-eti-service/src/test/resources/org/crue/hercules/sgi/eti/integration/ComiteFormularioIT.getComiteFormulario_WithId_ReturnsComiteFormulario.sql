-- COMITÉ
INSERT INTO eti.comite (id, comite, activo) VALUES (1, 'Comite1', true);

-- FORMULARIO 
INSERT INTO eti.formulario (id, nombre, descripcion, activo) VALUES (1, 'M10', 'Descripcion', true);

-- COMITE FORMULARIO 
INSERT INTO eti.comite_formulario (id, comite_id, formulario_id) VALUES (1, 1, 1);

