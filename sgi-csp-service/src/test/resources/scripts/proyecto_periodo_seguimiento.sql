-- DEPENDENCIAS: proyecto 
/*
  scripts = { 
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/estado_proyecto.sql",    
    "classpath:scripts/proyecto.sql", 
  }
*/

INSERT INTO csp.proyecto_periodo_seguimiento
(id, proyecto_id, num_periodo, observaciones, fecha_inicio, fecha_fin, fecha_inicio_presentacion, fecha_fin_presentacion)
VALUES(1, 1, 1, 'obs-001', '2020-10-01', '2020-10-05', '2020-10-02', '2020-10-04');

INSERT INTO csp.proyecto_periodo_seguimiento
(id, proyecto_id, num_periodo, observaciones, fecha_inicio, fecha_fin, fecha_inicio_presentacion, fecha_fin_presentacion)
VALUES(2, 1, 2, 'obs-002', '2020-10-05', '2020-11-02', '2020-10-06', '2020-11-01');

INSERT INTO csp.proyecto_periodo_seguimiento
(id, proyecto_id, num_periodo, observaciones, fecha_inicio, fecha_fin, fecha_inicio_presentacion, fecha_fin_presentacion)
VALUES(3, 1, 3, 'obs-003', '2020-11-03', '2020-12-03', '2020-11-04', '2020-12-02');

INSERT INTO csp.proyecto_periodo_seguimiento
(id, proyecto_id, num_periodo, observaciones, fecha_inicio, fecha_fin, fecha_inicio_presentacion, fecha_fin_presentacion)
VALUES(4, 1, 4, 'obs-4', '2020-12-04', '2020-12-14', '2020-12-05', '2020-12-13');

INSERT INTO csp.proyecto_periodo_seguimiento
(id, proyecto_id, num_periodo, observaciones, fecha_inicio, fecha_fin, fecha_inicio_presentacion, fecha_fin_presentacion)
VALUES(5, 1, 5, 'obs-5', '2020-12-14', '2020-12-30', '2020-12-15', '2020-12-29');