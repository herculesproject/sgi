-- DEPENDENCIAS:
/*
scripts = {
    // @formatter:off
    "classpath:scripts/rol_proyecto.sql",
    "classpath:scripts/tipo-grupo.sql",
    "classpath:scripts/grupo.sql"
    // @formatter:on
  }
*/

INSERT INTO test.grupo_relacion_institucional (id, grupo_id, entidad_ref, institucion) VALUES (1, 1, 'ENT-001', NULL);
INSERT INTO test.grupo_relacion_institucional (id, grupo_id, entidad_ref, institucion) VALUES (2, 1, NULL, 'Universidad de Ejemplo');
INSERT INTO test.grupo_relacion_institucional (id, grupo_id, entidad_ref, institucion) VALUES (3, 2, 'ENT-002', NULL);

ALTER SEQUENCE test.grupo_relacion_institucional_seq RESTART WITH 4;
