-- DEPENDENCIAS
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
INSERT INTO test.convocatoria_periodo_justificacion (id, convocatoria_id, num_periodo, mes_inicial, mes_final, fecha_inicio_presentacion, fecha_fin_presentacion, tipo) 
VALUES 
  (1, 1, 1, 1, 2, '2020-10-10T00:00:00Z', '2020-11-20T23:59:59Z', 'PERIODICO'),
  (2, 1, 2, 10, 21, '2020-10-10T00:00:00Z', '2020-11-20T23:59:59Z', 'PERIODICO');

INSERT INTO test.convocatoria_periodo_justificacion_observaciones (convocatoria_periodo_justificacion_id, lang, value_) 
VALUES 
  (1, 'es', 'observaciones-001'),
  (2, 'es', 'observaciones-002');

ALTER SEQUENCE test.convocatoria_periodo_justificacion_seq RESTART WITH 3;
