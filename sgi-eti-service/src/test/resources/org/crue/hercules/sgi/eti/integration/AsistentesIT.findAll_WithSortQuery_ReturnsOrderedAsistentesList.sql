-- COMITE
INSERT INTO eti.comite (id, comite, activo) VALUES (1, 'Comite1', true);

-- TIPO CONVOCATORIA REUNION 
INSERT INTO eti.tipo_convocatoria_reunion (id, nombre, activo) VALUES (1, 'Ordinaria', true);

-- CONVOCATORIA REUNION
INSERT INTO eti.convocatoria_reunion
(id, comite_id, fecha_evaluacion, fecha_limite, Lugar, orden_dia, codigo, tipo_convocatoria_reunion_id, hora_inicio, minuto_inicio, fecha_envio, activo)
VALUES(1, 1, '2020-07-01 00:00:00.000', '2020-08-01', 'Lugar 01', 'Orden del día convocatoria reunión 01', 'CR-01', 1, 8, 30, '2020-07-13', true);

-- CARGO COMITE
INSERT INTO eti.cargo_comite (id, nombre, activo) VALUES (1, 'CargoComite1', true);

-- EVALUADOR
INSERT INTO eti.evaluador (id, resumen, comite_id, cargo_comite_id, fecha_alta, fecha_baja, usuario_ref, activo)
VALUES (1, 'Evaluador1', 1, 1, '2020-07-01', '2021-07-01', 'user-001', true);

-- ASISTENTES
INSERT INTO eti.asistentes (id, evaluador_id, convocatoria_reunion_id, motivo, asistencia)
VALUES (1,  1, 1,  'Motivo1', true);
INSERT INTO eti.asistentes (id, evaluador_id, convocatoria_reunion_id, motivo, asistencia)
VALUES (2,  1, 1,  'Motivo2', true);
INSERT INTO eti.asistentes (id, evaluador_id, convocatoria_reunion_id, motivo, asistencia)
VALUES (3,  1, 1,  'Motivo3', true);
INSERT INTO eti.asistentes (id, evaluador_id, convocatoria_reunion_id, motivo, asistencia)
VALUES (4,  1, 1,  'Motivo4', true);
INSERT INTO eti.asistentes (id, evaluador_id, convocatoria_reunion_id, motivo, asistencia)
VALUES (5,  1, 1,  'Motivo5', true);
INSERT INTO eti.asistentes (id, evaluador_id, convocatoria_reunion_id, motivo, asistencia)
VALUES (6,  1, 1,  'Motivo6', true);