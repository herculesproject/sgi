-- DEPENDENCIAS: proyecto
/*
  scripts = {
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/estado_proyecto.sql",
    "classpath:scripts/proyecto.sql",
     "classpath:scripts/rol_proyecto.sql"
  }
*/

INSERT INTO test.proyecto_equipo
(id, proyecto_id, persona_ref, rol_proyecto_id, fecha_inicio, fecha_fin, horas_dedicacion, created_by, creation_date, last_modified_by, last_modified_date)
VALUES(100, 1, 'ref-001', 1, '2020-01-01T00:00:00Z', '2020-01-15T23:59:59Z', 1, 'admin', '2020-01-01T00:00:00Z', 'admin', '2020-01-01T00:00:00Z');

INSERT INTO test.proyecto_equipo
(id, proyecto_id, persona_ref, rol_proyecto_id, fecha_inicio, fecha_fin, horas_dedicacion, created_by, creation_date, last_modified_by, last_modified_date)
VALUES(101, 1, 'ref-002', 1, '2020-02-01T00:00:00Z', '2020-02-15T23:59:59Z', 2, 'admin', '2020-01-01T00:00:00Z', 'admin', '2020-01-01T00:00:00Z');

INSERT INTO test.proyecto_equipo
(id, proyecto_id, persona_ref, rol_proyecto_id, fecha_inicio, fecha_fin, horas_dedicacion, created_by, creation_date, last_modified_by, last_modified_date)
VALUES(102, 2, 'ref-003', 1, '2020-03-01T00:00:00Z', '2020-03-15T23:59:59Z', 3, 'admin', '2020-01-01T00:00:00Z', 'admin', '2020-01-01T00:00:00Z');

INSERT INTO test.proyecto_equipo
(id, proyecto_id, persona_ref, rol_proyecto_id, fecha_inicio, fecha_fin, horas_dedicacion, created_by, creation_date, last_modified_by, last_modified_date)
VALUES(103, 1, 'ref-004', 1, '2020-04-01T00:00:00Z', '2020-04-15T23:59:59Z', 4, 'admin', '2020-01-01T00:00:00Z', 'admin', '2020-01-01T00:00:00Z');

INSERT INTO test.proyecto_equipo
(id, proyecto_id, persona_ref, rol_proyecto_id, fecha_inicio, fecha_fin, horas_dedicacion, created_by, creation_date, last_modified_by, last_modified_date)
VALUES(104, 1, 'ref-5', 1, '2020-05-01T00:00:00Z', '2020-05-15T23:59:59Z', 5, 'admin', '2020-01-01T00:00:00Z', 'admin', '2020-01-01T00:00:00Z');

INSERT INTO test.proyecto_equipo
(id, proyecto_id, persona_ref, rol_proyecto_id, fecha_inicio, fecha_fin, horas_dedicacion, created_by, creation_date, last_modified_by, last_modified_date)
VALUES(105, 1, 'user', 1, '2021-05-01T00:00:00Z', '2099-05-15T23:59:59Z', 6, 'admin', '2020-01-01T00:00:00Z', 'admin', '2020-01-01T00:00:00Z');

INSERT INTO test.proyecto_equipo
(id, proyecto_id, persona_ref, rol_proyecto_id, fecha_inicio, fecha_fin, horas_dedicacion, created_by, creation_date, last_modified_by, last_modified_date)
VALUES(106, 1, '001', 1, '2020-03-01T00:00:00Z', '2020-03-31T23:59:59Z', null, 'test-user', '2020-01-01T00:00:00Z', 'test-user', '2020-01-01T00:00:00Z');

INSERT INTO test.proyecto_equipo
(id, proyecto_id, persona_ref, rol_proyecto_id, fecha_inicio, fecha_fin, horas_dedicacion, created_by, creation_date, last_modified_by, last_modified_date)
VALUES(201, 2, 'user', 1, '2020-01-01T00:00:00Z', '2020-01-15T23:59:59Z', 1, 'admin', '2020-01-01T00:00:00Z', 'admin', '2020-01-01T00:00:00Z');

