-- COMITÉ
INSERT INTO eti.comite (id, comite, activo) VALUES (1, 'Comite1', true);

-- FORMULARIO 
INSERT INTO eti.formulario (id, nombre, descripcion, activo) VALUES (1, 'M10', 'Descripcion', true);

-- COMITE FORMULARIO 
INSERT INTO eti.comite_formulario (id, comite_id, formulario_id) VALUES (1, 1, 1);
INSERT INTO eti.comite_formulario (id, comite_id, formulario_id) VALUES (2, 1, 1);
INSERT INTO eti.comite_formulario (id, comite_id, formulario_id) VALUES (3, 1, 1);
INSERT INTO eti.comite_formulario (id, comite_id, formulario_id) VALUES (4, 1, 1);
INSERT INTO eti.comite_formulario (id, comite_id, formulario_id) VALUES (5, 1, 1);
INSERT INTO eti.comite_formulario (id, comite_id, formulario_id) VALUES (6, 1, 1);
INSERT INTO eti.comite_formulario (id, comite_id, formulario_id) VALUES (7, 1, 1);
INSERT INTO eti.comite_formulario (id, comite_id, formulario_id) VALUES (8, 1, 1);
