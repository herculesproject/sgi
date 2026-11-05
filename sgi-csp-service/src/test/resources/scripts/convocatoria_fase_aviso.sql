-- DEPENDENCIAS: convocatoria_fase
/*
  scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/tipo_fase.sql",
    "classpath:scripts/convocatoria_fase.sql"
    // @formatter:on
  }
*/
INSERT INTO test.convocatoria_fase_aviso
(id, comunicado_ref, tarea_programada_ref, incluir_ips_solicitud, incluir_ips_proyecto)
VALUES
(1, '1', '1', false, false),
(2, '2', '2', false, false);

UPDATE test.convocatoria_fase SET convocatoria_fase_aviso1_id = 1, convocatoria_fase_aviso2_id = 2 WHERE id = 1;
