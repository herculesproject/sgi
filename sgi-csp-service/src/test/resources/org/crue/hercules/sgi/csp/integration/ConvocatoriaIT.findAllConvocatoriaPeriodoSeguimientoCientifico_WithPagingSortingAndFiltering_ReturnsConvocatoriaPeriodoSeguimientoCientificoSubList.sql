-- MODELO EJECUCION
INSERT INTO csp.modelo_ejecucion (id, nombre, descripcion, activo) VALUES (1, 'nombre-1', 'descripcion-1', true);

-- MODELO UNIDAD
INSERT INTO csp.modelo_unidad (id, unidad_gestion_ref, modelo_ejecucion_id, activo) VALUES (1, 'unidad-001', 1, true);

-- TIPO_FINALIDAD
INSERT INTO csp.tipo_finalidad (id,nombre,descripcion,activo) VALUES (1,'nombre-1','descripcion-1',true);

-- MODELO TIPO FINALIDAD
INSERT INTO csp.modelo_tipo_finalidad (id, modelo_ejecucion_id, tipo_finalidad_id, activo) VALUES (1, 1, 1, true);

-- TIPO_REGIMEN_CONCURRENCIA
INSERT INTO csp.tipo_regimen_concurrencia (id,nombre,activo) VALUES (1,'nombre-1',true);

-- TIPO AMBITO GEOGRAFICO
INSERT INTO csp.tipo_ambito_geografico (id, nombre, activo) VALUES (1, 'nombre-001', true);

-- CONVOCATORIA
INSERT INTO csp.convocatoria
(id, unidad_gestion_ref, modelo_ejecucion_id, codigo, anio, titulo, objeto, observaciones, tipo_finalidad_id, tipo_regimen_concurrencia_id, destinatarios, colaborativos, estado, duracion, tipo_ambito_geografico_id, clasificacion_cvn, activo)
VALUES(1, 'unidad-001', 1, 'codigo-001', 2020, 'titulo-001', 'objeto-001', 'observaciones-001', 1, 1, 'INDIVIDUAL', true, 'REGISTRADA', 24, 1, 'AYUDAS', true);

-- CONVOCATORIA PERIODO SEGUIMIENTO CIENTIFICO
insert into csp.convocatoria_periodo_seguimiento_cientifico
(id, convocatoria_id, num_periodo, mes_inicial, mes_final, fecha_inicio_presentacion, fecha_fin_presentacion, observaciones)
values(1, 1, 1, 1, 2, '2020-01-01', '2020-02-01', 'observaciones-001');

insert into csp.convocatoria_periodo_seguimiento_cientifico
(id, convocatoria_id, num_periodo, mes_inicial, mes_final, fecha_inicio_presentacion, fecha_fin_presentacion, observaciones)
values(2, 1, 2, 3, 4, '2020-03-01', '2020-04-01', 'observaciones-002');

insert into csp.convocatoria_periodo_seguimiento_cientifico
(id, convocatoria_id, num_periodo, mes_inicial, mes_final, fecha_inicio_presentacion, fecha_fin_presentacion, observaciones)
values(3, 1, 3, 5, 6, '2020-05-01', '2020-06-01', 'observaciones-003');

insert into csp.convocatoria_periodo_seguimiento_cientifico
(id, convocatoria_id, num_periodo, mes_inicial, mes_final, fecha_inicio_presentacion, fecha_fin_presentacion, observaciones)
values(4, 1, 4, 7, 8, '2020-07-01', '2020-08-01', 'observaciones-04');
insert into csp.convocatoria_periodo_seguimiento_cientifico

(id, convocatoria_id, num_periodo, mes_inicial, mes_final, fecha_inicio_presentacion, fecha_fin_presentacion, observaciones)
values(5, 1, 5, 9, 10, '2020-09-01', '2020-10-01', 'observaciones-05');

insert into csp.convocatoria_periodo_seguimiento_cientifico
(id, convocatoria_id, num_periodo, mes_inicial, mes_final, fecha_inicio_presentacion, fecha_fin_presentacion, observaciones)
values(6, 1, 6, 11, 12, '2020-11-01', '2020-12-01', 'observaciones-06');

insert into csp.convocatoria_periodo_seguimiento_cientifico
(id, convocatoria_id, num_periodo, mes_inicial, mes_final, fecha_inicio_presentacion, fecha_fin_presentacion, observaciones)
values(7, 1, 13, 14, 8, '2021-01-01', '2021-02-01', 'observaciones-07');

insert into csp.convocatoria_periodo_seguimiento_cientifico
(id, convocatoria_id, num_periodo, mes_inicial, mes_final, fecha_inicio_presentacion, fecha_fin_presentacion, observaciones)
values(8, 1, 8, 15, 16, '2021-03-01', '2021-04-01', 'observaciones-08');
