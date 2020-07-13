-- COMITE
INSERT INTO eti.comite (id, comite, activo) VALUES (1, 'Comite1', true);
INSERT INTO eti.comite (id, comite, activo) VALUES (2, 'Comite2', true);
INSERT INTO eti.comite (id, comite, activo) VALUES (3, 'Comite3', true);
INSERT INTO eti.comite (id, comite, activo) VALUES (4, 'Comite4', true);
INSERT INTO eti.comite (id, comite, activo) VALUES (5, 'Comite5', true);
INSERT INTO eti.comite (id, comite, activo) VALUES (6, 'Comite6', true);
INSERT INTO eti.comite (id, comite, activo) VALUES (7, 'Comite7', true);
INSERT INTO eti.comite (id, comite, activo) VALUES (8, 'Comite8', true);

-- CARGO COMITE
INSERT INTO eti.cargo_comite (id, nombre, activo) VALUES (1, 'CargoComite1', true);
INSERT INTO eti.cargo_comite (id, nombre, activo) VALUES (2, 'CargoComite2', true);
INSERT INTO eti.cargo_comite (id, nombre, activo) VALUES (3, 'CargoComite3', true);
INSERT INTO eti.cargo_comite (id, nombre, activo) VALUES (4, 'CargoComite4', true);
INSERT INTO eti.cargo_comite (id, nombre, activo) VALUES (5, 'CargoComite5', true);
INSERT INTO eti.cargo_comite (id, nombre, activo) VALUES (6, 'CargoComite6', true);
INSERT INTO eti.cargo_comite (id, nombre, activo) VALUES (7, 'CargoComite7', true);
INSERT INTO eti.cargo_comite (id, nombre, activo) VALUES (8, 'CargoComite8', true);

-- EVALUADOR
INSERT INTO eti.evaluador (id, resumen, comite_id, cargo_comite_id, fecha_alta, fecha_baja, usuario_ref, activo)
VALUES (1, 'Evaluador1', 1, 1, '2020-07-01', '2021-07-01', 'user-001', true);
INSERT INTO eti.evaluador (id, resumen, comite_id, cargo_comite_id, fecha_alta, fecha_baja, usuario_ref, activo)
VALUES (2, 'Evaluador2', 2, 2, '2020-07-01', '2021-07-01', 'user-002', true);
INSERT INTO eti.evaluador (id, resumen, comite_id, cargo_comite_id, fecha_alta, fecha_baja, usuario_ref, activo)
VALUES (3, 'Evaluador3', 3, 3, '2020-07-01', '2021-07-01', 'user-003', true);
INSERT INTO eti.evaluador (id, resumen, comite_id, cargo_comite_id, fecha_alta, fecha_baja, usuario_ref, activo)
VALUES (4, 'Evaluador4', 4, 4, '2020-07-01', '2021-07-01', 'user-004', true);
INSERT INTO eti.evaluador (id, resumen, comite_id, cargo_comite_id, fecha_alta, fecha_baja, usuario_ref, activo)
VALUES (5, 'Evaluador5', 5, 5, '2020-07-01', '2021-07-01', 'user-005', true);
INSERT INTO eti.evaluador (id, resumen, comite_id, cargo_comite_id, fecha_alta, fecha_baja, usuario_ref, activo)
VALUES (6, 'Evaluador6', 6, 6, '2020-07-01', '2021-07-01', 'user-006', true);
INSERT INTO eti.evaluador (id, resumen, comite_id, cargo_comite_id, fecha_alta, fecha_baja, usuario_ref, activo)
VALUES (7, 'Evaluador7', 7, 7, '2020-07-01', '2021-07-01', 'user-007', true);
INSERT INTO eti.evaluador (id, resumen, comite_id, cargo_comite_id, fecha_alta, fecha_baja, usuario_ref, activo)
VALUES (8, 'Evaluador8', 8, 8, '2020-07-01', '2021-07-01', 'user-008', true);
