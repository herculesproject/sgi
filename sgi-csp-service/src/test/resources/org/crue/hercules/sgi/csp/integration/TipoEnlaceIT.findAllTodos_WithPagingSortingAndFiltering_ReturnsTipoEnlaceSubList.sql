-- TIPO_ENLACE
insert into test.tipo_enlace (id,descripcion,activo) values (1,'descripcion-001',true);
insert into test.tipo_enlace (id,descripcion,activo) values (2,'descripcion-002',true);
insert into test.tipo_enlace (id,descripcion,activo) values (3,'descripcion-003',false);
insert into test.tipo_enlace (id,descripcion,activo) values (4,'descripcion-011',true);
insert into test.tipo_enlace (id,descripcion,activo) values (5,'descripcion-012',true);
insert into test.tipo_enlace (id,descripcion,activo) values (6,'descripcion-013',false);

-- TIPO_ENLACE_NOMBRE
INSERT INTO test.tipo_enlace_nombre (tipo_enlace_id, lang, value_) VALUES(1, 'es', 'nombre-001');
INSERT INTO test.tipo_enlace_nombre (tipo_enlace_id, lang, value_) VALUES(2, 'es', 'nombre-002');
INSERT INTO test.tipo_enlace_nombre (tipo_enlace_id, lang, value_) VALUES(3, 'es', 'nombre-003');
INSERT INTO test.tipo_enlace_nombre (tipo_enlace_id, lang, value_) VALUES(4, 'es', 'nombre-011');
INSERT INTO test.tipo_enlace_nombre (tipo_enlace_id, lang, value_) VALUES(5, 'es', 'nombre-012');
INSERT INTO test.tipo_enlace_nombre (tipo_enlace_id, lang, value_) VALUES(6, 'es', 'nombre-013');