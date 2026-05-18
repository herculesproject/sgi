-- DEPENDENCIAS:
/*
scripts = {
    // @formatter:off
    "classpath:scripts/rol_proyecto.sql",
    "classpath:scripts/tipo-grupo.sql",
    "classpath:scripts/grupo.sql",
    "classpath:scripts/tipo-descriptor-grupo.sql"
    // @formatter:on
  }
*/

INSERT INTO test.grupo_descriptor (id, grupo_id, tipo_descriptor_grupo_id) VALUES (1, 1, 1);
INSERT INTO test.grupo_descriptor (id, grupo_id, tipo_descriptor_grupo_id) VALUES (2, 1, 2);
INSERT INTO test.grupo_descriptor (id, grupo_id, tipo_descriptor_grupo_id) VALUES (3, 2, 1);

INSERT INTO test.grupo_descriptor_texto (grupo_descriptor_id, lang, value_) VALUES (1, 'es', 'Descripcion oferta cientifica grupo 1');
INSERT INTO test.grupo_descriptor_texto (grupo_descriptor_id, lang, value_) VALUES (2, 'es', 'Descripcion equipamiento grupo 1');
INSERT INTO test.grupo_descriptor_texto (grupo_descriptor_id, lang, value_) VALUES (3, 'es', 'Descripcion oferta cientifica grupo 2');

ALTER SEQUENCE test.grupo_descriptor_seq RESTART WITH 4;
