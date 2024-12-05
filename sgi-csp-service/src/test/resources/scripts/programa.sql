-- PROGRAMA
INSERT INTO test.programa (id, descripcion, programa_padre_id, activo) VALUES (1, 'raiz-1',               null, true);
INSERT INTO test.programa (id, descripcion, programa_padre_id, activo) VALUES (2, 'raiz-2',               1,    false);
INSERT INTO test.programa (id, descripcion, programa_padre_id, activo) VALUES (3, 'raiz-3',               1,    true);
INSERT INTO test.programa (id, descripcion, programa_padre_id, activo) VALUES (4, 'raiz-1-hijo-1',        1,    true);
INSERT INTO test.programa (id, descripcion, programa_padre_id, activo) VALUES (5, 'raiz-1-hijo-2',        1,    false);
INSERT INTO test.programa (id, descripcion, programa_padre_id, activo) VALUES (6, 'raiz-1-hijo-3',        1,    true);
INSERT INTO test.programa (id, descripcion, programa_padre_id, activo) VALUES (7, 'raiz-2-hijo-1',        2,    true);
INSERT INTO test.programa (id, descripcion, programa_padre_id, activo) VALUES (8, 'raiz-1-hijo-1-hijo-1', 4,    true);

-- PROGRAMA_NOMBRE
INSERT INTO test.programa_nombre (programa_id, lang, value_) VALUES (1,  'es', 'r1');
INSERT INTO test.programa_nombre (programa_id, lang, value_) VALUES (2,  'es', 'r2');
INSERT INTO test.programa_nombre (programa_id, lang, value_) VALUES (3,  'es', 'r3');
INSERT INTO test.programa_nombre (programa_id, lang, value_) VALUES (4,  'es', 'r1h1');
INSERT INTO test.programa_nombre (programa_id, lang, value_) VALUES (5,  'es', 'r1h2');
INSERT INTO test.programa_nombre (programa_id, lang, value_) VALUES (6,  'es', 'r1h3');
INSERT INTO test.programa_nombre (programa_id, lang, value_) VALUES (7,  'es', 'r2h1');
INSERT INTO test.programa_nombre (programa_id, lang, value_) VALUES (8,  'es', 'r1h1h1');
