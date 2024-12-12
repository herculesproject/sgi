-- AREA TEMATICA
INSERT INTO test.area_tematica (id, descripcion, area_tematica_padre_id, activo) VALUES (1, 'descripcion-001', null, true);
INSERT INTO test.area_tematica (id, descripcion, area_tematica_padre_id, activo) VALUES (2, 'descripcion-001', 1, true);
INSERT INTO test.area_tematica (id, descripcion, area_tematica_padre_id, activo) VALUES (3, 'descripcion-001', 1, true);
INSERT INTO test.area_tematica (id, descripcion, area_tematica_padre_id, activo) VALUES (4, 'descripcion-001', 1, true);
INSERT INTO test.area_tematica (id, descripcion, area_tematica_padre_id, activo) VALUES (5, 'descripcion-001', 1, false);
INSERT INTO test.area_tematica (id, descripcion, area_tematica_padre_id, activo) VALUES (6, 'descripcion-001', 2, true);
INSERT INTO test.area_tematica (id, descripcion, area_tematica_padre_id, activo) VALUES (11, 'descripcion-001', null, true);
INSERT INTO test.area_tematica (id, descripcion, area_tematica_padre_id, activo) VALUES (12, 'descripcion-001', 11, true);
INSERT INTO test.area_tematica (id, descripcion, area_tematica_padre_id, activo) VALUES (13, 'descripcion-001', 12, true);


-- AREA_TEMATICA_NOMBRE
INSERT INTO test.area_tematica_nombre (area_tematica_id, lang, value_) VALUES (1, 'es', 'nombre-001');
INSERT INTO test.area_tematica_nombre (area_tematica_id, lang, value_) VALUES (2, 'es', 'A-002');
INSERT INTO test.area_tematica_nombre (area_tematica_id, lang, value_) VALUES (3, 'es', 'A-003');
INSERT INTO test.area_tematica_nombre (area_tematica_id, lang, value_) VALUES (4, 'es', 'A-004');
INSERT INTO test.area_tematica_nombre (area_tematica_id, lang, value_) VALUES (5, 'es', 'A-005');
INSERT INTO test.area_tematica_nombre (area_tematica_id, lang, value_) VALUES (6, 'es', 'A-006');
INSERT INTO test.area_tematica_nombre (area_tematica_id, lang, value_) VALUES (11, 'es', 'nombre-011');
INSERT INTO test.area_tematica_nombre (area_tematica_id, lang, value_) VALUES (12, 'es', 'A-012');
INSERT INTO test.area_tematica_nombre (area_tematica_id, lang, value_) VALUES (13, 'es', 'A-013');

