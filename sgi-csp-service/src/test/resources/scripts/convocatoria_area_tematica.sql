-- DEPENDENCIAS: modelo_ejecucion, tipo_finalidad, tipo_regimen_concurrencia, tipo_ambito_geografico, convocatoria, area_tematica
/*
  scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/area_tematica.sql"
    // @formatter:on
  }
*/
INSERT INTO test.convocatoria_area_tematica (id, convocatoria_id, area_tematica_id, observaciones)
VALUES (1, 1, 1, 'observaciones-area-tematica-001');

ALTER SEQUENCE test.convocatoria_area_tematica_seq RESTART WITH 2;
