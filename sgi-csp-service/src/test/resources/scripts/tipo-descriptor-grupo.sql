INSERT INTO test.tipo_descriptor_grupo (id, activo)
VALUES
(1, true),
(2, true),
(3, true),
(4, false);

INSERT INTO test.tipo_descriptor_grupo_nombre (tipo_descriptor_grupo_id, lang, value_) VALUES (1, 'es', 'Oferta cientifica');
INSERT INTO test.tipo_descriptor_grupo_nombre (tipo_descriptor_grupo_id, lang, value_) VALUES (2, 'es', 'Equipamiento');
INSERT INTO test.tipo_descriptor_grupo_nombre (tipo_descriptor_grupo_id, lang, value_) VALUES (3, 'es', 'Servicios');
INSERT INTO test.tipo_descriptor_grupo_nombre (tipo_descriptor_grupo_id, lang, value_) VALUES (4, 'es', 'Inactivo');

ALTER SEQUENCE test.tipo_descriptor_grupo_seq RESTART WITH 5;
