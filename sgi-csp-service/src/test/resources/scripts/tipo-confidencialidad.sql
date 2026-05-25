INSERT INTO test.tipo_confidencialidad (id, activo)
VALUES
(1, true),
(2, true),
(3, false);

INSERT INTO test.tipo_confidencialidad_nombre (tipo_confidencialidad_id, lang, value_) VALUES (1, 'es', 'Confidencial');
INSERT INTO test.tipo_confidencialidad_nombre (tipo_confidencialidad_id, lang, value_) VALUES (2, 'es', 'No confidencial');
INSERT INTO test.tipo_confidencialidad_nombre (tipo_confidencialidad_id, lang, value_) VALUES (3, 'es', 'Inactivo');

ALTER SEQUENCE test.tipo_confidencialidad_seq RESTART WITH 4;
