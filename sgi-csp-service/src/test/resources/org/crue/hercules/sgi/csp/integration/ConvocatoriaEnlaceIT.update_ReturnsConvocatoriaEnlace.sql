-- CONVOCATORIA
INSERT INTO csp.convocatoria (id, codigo, activo) VALUES (1, 'codigo-001', true);

--TIPO ENLACE
insert into csp.tipo_enlace (id,nombre,descripcion,activo) values (1,'nombre-1','descripcion-1',true);

--CONVOCATORIA ENLACE
INSERT INTO csp.convocatoria_enlace (id, convocatoria_id, url, descripcion, tipo_enlace_id) values(1, 1, 'www.url1.com','descripcion-1', 1);