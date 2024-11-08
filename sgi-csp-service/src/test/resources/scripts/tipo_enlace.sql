-- DEPENDENCIAS:
/*

*/

INSERT INTO test.tipo_enlace
(id, activo, descripcion)
VALUES
(1, true, NULL),
(2, true, NULL),
(3, true, NULL);

-- TIPO_ENLACE_NOMBRE
INSERT INTO test.tipo_enlace_nombre (tipo_enlace_id, lang, value_) VALUES(1, 'es', 'Publicación convocatoria');
INSERT INTO test.tipo_enlace_nombre (tipo_enlace_id, lang, value_) VALUES(2, 'es', 'Presentación solicitudes');
INSERT INTO test.tipo_enlace_nombre (tipo_enlace_id, lang, value_) VALUES(3, 'es', 'Portal de justificación');