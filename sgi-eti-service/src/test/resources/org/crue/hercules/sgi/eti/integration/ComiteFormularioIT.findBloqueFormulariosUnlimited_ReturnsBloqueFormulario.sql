

-- TIPO EVALUACION 
INSERT INTO eti.tipo_evaluacion (id, nombre, activo) VALUES (1, 'TipoEvaluacion1', true);
INSERT INTO eti.tipo_evaluacion (id, nombre, activo) VALUES (2, 'TipoEvaluacion2', true);

-- COMITÉ
INSERT INTO eti.comite (id, comite, activo) VALUES (1, 'Comite1', true);

-- FORMULARIO 
INSERT INTO eti.formulario (id, nombre, descripcion, activo) VALUES (1, 'M10', 'Descripcion', true);
INSERT INTO eti.formulario (id, nombre, descripcion, activo) VALUES (2, 'M20', 'Descripcion', true);

-- COMITE FORMULARIO 
INSERT INTO eti.comite_formulario (id, comite_id, formulario_id) VALUES (1, 1, 1);


-- BLOQUE FORMULARIO
INSERT INTO eti.bloque_formulario (id, nombre, formulario_id, orden, activo) VALUES (1, 'BloqueFormulario1', 1, 8, true);
INSERT INTO eti.bloque_formulario (id, nombre, formulario_id, orden, activo) VALUES (2, 'BloqueFormulario2', 1, 7, true);