
-- DEPENDENCIAS: solicitud
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

INSERT INTO csp.solicitud_proyecto_datos
  (id, solicitud_id, titulo, colaborativo, presupuesto_por_entidades) 
VALUES
  (1, 1, 'titulo-001', true, true),
  (2, 2, 'titulo-002', true, true),
  (3, 3, 'titulo-003', true, false),
  (4, 4, 'titulo-004', false, false);
