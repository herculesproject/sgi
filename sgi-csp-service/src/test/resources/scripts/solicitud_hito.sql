-- DEPENDENCIAS: solicitud, tipo_hito
/*
  scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/solicitud.sql",
    "classpath:scripts/tipo_hito.sql"
    // @formatter:on
  }
*/
INSERT INTO test.solicitud_hito
(id, solicitud_id, tipo_hito_id, fecha)
VALUES
(1, 1, 1, '2022-01-27T11:11:11.000Z'),
(2, 1, 2, '2022-01-28T11:11:11.000Z');
