-- AREA TEMATICA
INSERT INTO test.area_tematica (id, descripcion, area_tematica_padre_id, activo) VALUES (1, 'descripcion-001', null, false);
INSERT INTO test.area_tematica (id, descripcion, area_tematica_padre_id, activo) VALUES (2, 'descripcion-001', 1, true);
INSERT INTO test.area_tematica (id, descripcion, area_tematica_padre_id, activo) VALUES (3, 'descripcion-001', 1, true);
INSERT INTO test.area_tematica (id, descripcion, area_tematica_padre_id, activo) VALUES (4, 'descripcion-001', 3, true);
INSERT INTO test.area_tematica (id, descripcion, area_tematica_padre_id, activo) VALUES (11, 'descripcion-001', null, true);


-- AREA_TEMATICA_NOMBRE
INSERT INTO test.area_tematica_nombre (area_tematica_id, lang, value_) VALUES (1, 'es', 'nombre-001');
INSERT INTO test.area_tematica_nombre (area_tematica_id, lang, value_) VALUES (2, 'es', 'A-002');
INSERT INTO test.area_tematica_nombre (area_tematica_id, lang, value_) VALUES (3, 'es', 'A-003');
INSERT INTO test.area_tematica_nombre (area_tematica_id, lang, value_) VALUES (4, 'es', 'A-004');
INSERT INTO test.area_tematica_nombre (area_tematica_id, lang, value_) VALUES (11, 'es', 'nombre-011');

