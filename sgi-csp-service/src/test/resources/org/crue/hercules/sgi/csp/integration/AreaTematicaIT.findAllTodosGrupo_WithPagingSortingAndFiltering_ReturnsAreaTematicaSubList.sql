-- AREA TEMATICA
INSERT INTO test.area_tematica (id, descripcion, area_tematica_padre_id, activo) VALUES (1, 'descripcion-001', null, true);
INSERT INTO test.area_tematica (id, descripcion, area_tematica_padre_id, activo) VALUES (2, 'descripcion-002', null, true);
INSERT INTO test.area_tematica (id, descripcion, area_tematica_padre_id, activo) VALUES (3, 'descripcion-003', null, false);
INSERT INTO test.area_tematica (id, descripcion, area_tematica_padre_id, activo) VALUES (4, 'descripcion-004', 1, false);
INSERT INTO test.area_tematica (id, descripcion, area_tematica_padre_id, activo) VALUES (5, 'descripcion-005', 1, true);
INSERT INTO test.area_tematica (id, descripcion, area_tematica_padre_id, activo) VALUES (11, 'descripcion-011', null, true);
INSERT INTO test.area_tematica (id, descripcion, area_tematica_padre_id, activo) VALUES (12, 'descripcion-012', 11, true);
INSERT INTO test.area_tematica (id, descripcion, area_tematica_padre_id, activo) VALUES (13, 'descripcion-013', 12, true);


-- AREA_TEMATICA_NOMBRE
INSERT INTO test.area_tematica_nombre (area_tematica_id, lang, value_) VALUES (1, 'es', 'nombre-001');
INSERT INTO test.area_tematica_nombre (area_tematica_id, lang, value_) VALUES (2, 'es', 'nombre-002');
INSERT INTO test.area_tematica_nombre (area_tematica_id, lang, value_) VALUES (3, 'es', 'nombre-003');
INSERT INTO test.area_tematica_nombre (area_tematica_id, lang, value_) VALUES (4, 'es', 'A-004');
INSERT INTO test.area_tematica_nombre (area_tematica_id, lang, value_) VALUES (5, 'es', 'A-005');
INSERT INTO test.area_tematica_nombre (area_tematica_id, lang, value_) VALUES (11, 'es', 'nombre-011');
INSERT INTO test.area_tematica_nombre (area_tematica_id, lang, value_) VALUES (12, 'es', 'A-012');
INSERT INTO test.area_tematica_nombre (area_tematica_id, lang, value_) VALUES (13, 'es', 'A-013');

