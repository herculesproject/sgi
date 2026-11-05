-- DEPENDENCIAS: proyecto_hito
/*
  scripts = {
    // @formatter:off
    "classpath:scripts/tipo_hito.sql",
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/proyecto.sql",
    "classpath:scripts/estado_proyecto.sql",
    "classpath:scripts/modelo_tipo_hito.sql",
    "classpath:scripts/proyecto_hito.sql"
    // @formatter:on
  }
*/
INSERT INTO test.proyecto_hito_aviso
(id, comunicado_ref, tarea_programada_ref, incluir_ips_proyecto)
VALUES
(1, '11', '41', false);

UPDATE test.proyecto_hito SET proyecto_hito_aviso_id = 1 WHERE id = 1;
