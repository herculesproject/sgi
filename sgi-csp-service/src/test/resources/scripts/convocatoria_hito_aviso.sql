-- DEPENDENCIAS: convocatoria_hito
/*
  scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/tipo_hito.sql",
    "classpath:scripts/convocatoria_hito.sql"
    // @formatter:on
  }
*/
INSERT INTO test.convocatoria_hito_aviso
(id, comunicado_ref, tarea_programada_ref, incluir_ips_solicitud, incluir_ips_proyecto)
VALUES
(1, '3', '3', false, false);

UPDATE test.convocatoria_hito SET convocatoria_hito_aviso_id = 1 WHERE id = 1;
