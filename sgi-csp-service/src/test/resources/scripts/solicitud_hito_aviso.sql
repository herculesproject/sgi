-- DEPENDENCIAS: solicitud_hito
/*
  scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/solicitud.sql",
    "classpath:scripts/tipo_hito.sql",
    "classpath:scripts/solicitud_hito.sql"
    // @formatter:on
  }
*/
INSERT INTO test.solicitud_hito_aviso
(id, comunicado_ref, tarea_programada_ref, incluir_ips_solicitud)
VALUES
(1, '1', '1', false),
(2, '2', '2', false);

UPDATE test.solicitud_hito SET solicitud_hito_aviso_id = 1 WHERE id = 1;
UPDATE test.solicitud_hito SET solicitud_hito_aviso_id = 2 WHERE id = 2;
