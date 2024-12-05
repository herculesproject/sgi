-- PROGRAMA
INSERT INTO test.programa (id, descripcion, programa_padre_id, activo) VALUES (1, 'descripcion-001', null, true);
INSERT INTO test.programa (id, descripcion, programa_padre_id, activo) VALUES (2, 'descripcion-002', 1,   true);

-- PROGRAMA_NOMBRE
INSERT INTO test.programa_nombre (programa_id, lang, value_) VALUES (1,  'es', 'nombre-001');
INSERT INTO test.programa_nombre (programa_id, lang, value_) VALUES (2,  'es', 'nombre-002');
