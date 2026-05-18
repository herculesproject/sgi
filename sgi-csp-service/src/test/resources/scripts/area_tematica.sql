-- DEPENDENCIAS: (ninguna)
/*
  scripts = {
    // @formatter:off
    // @formatter:on
  }
*/
INSERT INTO test.area_tematica (id, area_tematica_padre_id, activo)
VALUES (1, null, true);

INSERT INTO test.area_tematica_nombre (area_tematica_id, lang, value_)
VALUES (1, 'es', 'Area Tematica 001');

ALTER SEQUENCE test.area_tematica_seq RESTART WITH 2;
