-- DEPENDENCIAS:
/*
scripts = {
    // @formatter:off
    "classpath:scripts/rol_proyecto.sql",
    "classpath:scripts/tipo-grupo.sql",
    "classpath:scripts/grupo.sql",
    "classpath:scripts/tipo_enlace.sql"
    // @formatter:on
  }
*/

INSERT INTO test.grupo_enlace (id, grupo_id, enlace, tipo_enlace_id) VALUES (1, 1, 'https://www.example', 1);
INSERT INTO test.grupo_enlace (id, grupo_id, enlace, tipo_enlace_id) VALUES (2, 1, 'https://other.example', 2);
INSERT INTO test.grupo_enlace (id, grupo_id, enlace, tipo_enlace_id) VALUES (3, 2, 'https://no-tipo.example', NULL);
INSERT INTO test.grupo_enlace (id, grupo_id, enlace, tipo_enlace_id) VALUES (4, 1, 'https://inactive.example', 4);

ALTER SEQUENCE test.grupo_enlace_seq RESTART WITH 5;
