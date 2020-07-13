-- COMITE
INSERT INTO eti.comite (id, comite, activo) VALUES (1, 'Comite1', true);
INSERT INTO eti.comite (id, comite, activo) VALUES (2, 'Comite2', true);

-- CARGO COMITE
INSERT INTO eti.cargo_comite (id, nombre, activo) VALUES (1, 'CargoComite1', true);
INSERT INTO eti.cargo_comite (id, nombre, activo) VALUES (2, 'CargoComite2', true);

-- EVALUADOR
INSERT INTO eti.evaluador (id, resumen, comite_id, cargo_comite_id, fecha_alta, fecha_baja, usuario_ref, activo)
VALUES (1, 'Evaluador1', 1, 1, '2020-07-01', '2021-07-01', 'user-001', true);
INSERT INTO eti.evaluador (id, resumen, comite_id, cargo_comite_id, fecha_alta, fecha_baja, usuario_ref, activo)
VALUES (2, 'Evaluador2', 2, 2, '2020-07-01', '2021-07-01', 'user-002', true);
