-- AREA TEMATICA
insert into test.area_tematica (id,descripcion,area_tematica_padre_id,activo) values (9999, 'descripcion-001', null, true);
insert into test.area_tematica (id,descripcion,area_tematica_padre_id,activo) values (8888, 'descripcion-001', 9999, true);

-- AREA_TEMATICA_NOMBRE
INSERT INTO test.area_tematica_nombre (area_tematica_id, lang, value_) VALUES (9999, 'es', 'nombre-001');
INSERT INTO test.area_tematica_nombre (area_tematica_id, lang, value_) VALUES (8888, 'es', 'A-8888');
