-- CONVOCATORIA
INSERT INTO csp.convocatoria (id, codigo, activo) VALUES (1, 'codigo-001', true);

--TIPO ENLACE
INSERT INTO csp.tipo_enlace (id,nombre,descripcion,activo) VALUES (1,'nombre-1','descripcion-1',true);

--CONVOCATORIA ENLACE
INSERT INTO  csp.convocatoria_enlace(id, convocatoria_id, url, descripcion, tipo_enlace_id) VALUES (1, 1, 'www.url1.com','descripcion-001', 1);
INSERT INTO  csp.convocatoria_enlace(id, convocatoria_id, url, descripcion, tipo_enlace_id) VALUES (2, 1, 'www.url1.com','descripcion-002', 1);
INSERT INTO  csp.convocatoria_enlace(id, convocatoria_id, url, descripcion, tipo_enlace_id) VALUES (3, 1, 'www.url1.com','descripcion-003', 1);
INSERT INTO  csp.convocatoria_enlace(id, convocatoria_id, url, descripcion, tipo_enlace_id) VALUES (4, 1, 'www.url1.com','descripcion-4', 1);
INSERT INTO  csp.convocatoria_enlace(id, convocatoria_id, url, descripcion, tipo_enlace_id) VALUES (5, 1, 'www.url1.com','descripcion-05', 1);
INSERT INTO  csp.convocatoria_enlace(id, convocatoria_id, url, descripcion, tipo_enlace_id) VALUES (6, 1, 'www.url1.com','descripcion-06', 1);
