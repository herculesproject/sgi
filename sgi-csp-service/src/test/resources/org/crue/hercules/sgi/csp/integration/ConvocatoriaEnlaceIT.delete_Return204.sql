-- CONVOCATORIA
INSERT INTO csp.convocatoria (id, codigo, activo) VALUES (1, 'codigo-001', true);

--TIPO ENLACE
INSERT INTO csp.tipo_enlace (id,nombre,descripcion,activo) VALUES (1,'nombre-1','descripcion-1',true);
--CONVOCATORIA ENLACE
INSERT INTO csp.convocatoria_enlace(id, convocatoria_id, url, descripcion, tipo_enlace_id) VALUES(1, 1, 'www.url1.com','descripcion-1',1);