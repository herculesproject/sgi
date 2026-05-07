INSERT INTO test.tipo_grupo (id, activo, created_by, creation_date, last_modified_by, last_modified_date)
VALUES
(1, true, 'test', '2021-01-01 22:59:59.000', 'test', '2021-01-01 22:59:59.000'),
(2, true, 'test', '2021-01-01 22:59:59.000', 'test', '2021-01-01 22:59:59.000'),
(3, true, 'test', '2021-01-01 22:59:59.000', 'test', '2021-01-01 22:59:59.000'),
(4, true, 'test', '2021-01-01 22:59:59.000', 'test', '2021-01-01 22:59:59.000');

INSERT INTO test.tipo_grupo_nombre (tipo_grupo_id, lang, value_) VALUES (1, 'es', 'Emergente');
INSERT INTO test.tipo_grupo_nombre (tipo_grupo_id, lang, value_) VALUES (2, 'es', 'Consolidado');
INSERT INTO test.tipo_grupo_nombre (tipo_grupo_id, lang, value_) VALUES (3, 'es', 'Precompetitivo');
INSERT INTO test.tipo_grupo_nombre (tipo_grupo_id, lang, value_) VALUES (4, 'es', 'Grupo de alto rendimiento');

ALTER SEQUENCE test.tipo_grupo_seq RESTART WITH 5;
