-- DEPENDENCIAS: modelo_ejecucion, tipo_finalidad, tipo_regimen_concurrencia, tipo_ambito_geografico, convocatoria
/*
  scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql"
    // @formatter:on
  }
*/
INSERT INTO test.convocatoria_periodo_seguimiento_cientifico (id, convocatoria_id, num_periodo, mes_inicial, mes_final, fecha_inicio_presentacion, fecha_fin_presentacion, tipo_seguimiento)
VALUES
  (1, 1, 1, 1, 6,  '2020-10-10T00:00:00Z', '2020-11-20T23:59:59Z', 'PERIODICO'),
  (2, 1, 2, 7, 12, '2021-01-10T00:00:00Z', '2021-02-20T23:59:59Z', 'FINAL');

INSERT INTO test.convocatoria_periodo_seguimiento_cientifico_observaciones (convocatoria_periodo_seguimiento_cientifico_id, lang, value_)
VALUES
  (1, 'es', 'observaciones-seguimiento-001'),
  (2, 'es', 'observaciones-seguimiento-002');

ALTER SEQUENCE test.convocatoria_periodo_seguimiento_cientifico_seq RESTART WITH 3;
