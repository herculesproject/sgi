-- DEPENDENCIAS: estado_solicitud, convocatoria
/*
  scripts = { 
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/estado_solicitud.sql",
    "classpath:scripts/solicitud.sql"
    // @formatter:on
  }
*/
UPDATE test.solicitud SET solicitante_ref = 'user';