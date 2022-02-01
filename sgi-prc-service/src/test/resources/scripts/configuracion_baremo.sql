-- CONFIGURACION_BAREMO
INSERT INTO test.configuracion_baremo
(id, created_by, creation_date, last_modified_by, last_modified_date, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(1, NULL, NULL, NULL, NULL, true, '060.030.070.000', 'Sexenios', 1, 'SEXENIO', 'OTRO_SISTEMA', 'PRINCIPAL', NULL);
INSERT INTO test.configuracion_baremo
(id, created_by, creation_date, last_modified_by, last_modified_date, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(2, NULL, NULL, NULL, NULL, true, '', 'Costes Indirectos', 1, 'COSTE_INDIRECTO', 'SGI', 'PRINCIPAL', NULL);
INSERT INTO test.configuracion_baremo
(id, created_by, creation_date, last_modified_by, last_modified_date, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(3, NULL, NULL, NULL, NULL, true, '', 'Producción científica', NULL, 'AGRUPADOR', '', '', NULL);
INSERT INTO test.configuracion_baremo
(id, created_by, creation_date, last_modified_by, last_modified_date, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(4, NULL, NULL, NULL, NULL, true, '', 'Libros', NULL, 'AGRUPADOR', '', '', 3);
INSERT INTO test.configuracion_baremo
(id, created_by, creation_date, last_modified_by, last_modified_date, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(5, NULL, NULL, NULL, NULL, true, '060.010.010.000', 'Autoría - BCI - Editorial extranjera', 1, 'AUTORIA_BCI _EDITORIAL_EXTRANJERA', 'CVN', 'PRINCIPAL', 4);
INSERT INTO test.configuracion_baremo
(id, created_by, creation_date, last_modified_by, last_modified_date, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(6, NULL, NULL, NULL, NULL, true, '060.010.010.000', 'Autoría - BCI - Editorial nacional', 2, 'AUTORIA_BCI _EDITORIAL_NACIONAL', 'CVN', 'PRINCIPAL', 4);
INSERT INTO test.configuracion_baremo
(id, created_by, creation_date, last_modified_by, last_modified_date, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(7, NULL, NULL, NULL, NULL, true, '060.010.010.000', 'Autoría - ICEE - Q1', 3, 'AUTORIA_ICEE_Q1', 'CVN', 'PRINCIPAL', 4);
INSERT INTO test.configuracion_baremo
(id, created_by, creation_date, last_modified_by, last_modified_date, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(8, NULL, NULL, NULL, NULL, true, '060.010.010.000', 'Autoría -  ICEE - Resto cuartiles', 4, 'AUTORIA_ICEE_RESTO_CUARTILES', 'CVN', 'PRINCIPAL', 4);
INSERT INTO test.configuracion_baremo
(id, created_by, creation_date, last_modified_by, last_modified_date, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(9, NULL, NULL, NULL, NULL, true, '060.010.010.000', 'Autoría - DIALNET', 5, 'AUTORIA_DIALNET', 'CVN', 'PRINCIPAL', 4);
INSERT INTO test.configuracion_baremo
(id, created_by, creation_date, last_modified_by, last_modified_date, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(10, NULL, NULL, NULL, NULL, true, '060.010.010.000', 'Autoría - Otras contribuciones', 6, 'AUTORIA_OTRAS', 'CVN', 'PRINCIPAL', 4);
INSERT INTO test.configuracion_baremo
(id, created_by, creation_date, last_modified_by, last_modified_date, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(11, NULL, NULL, NULL, NULL, true, '060.010.010.000', 'Capítulo de libro - BCI - Editorial extranjera', 7, 'CAP_LIBRO_BCI _EDITORIAL_EXTRANJERA', 'CVN', 'PRINCIPAL', 4);
INSERT INTO test.configuracion_baremo
(id, created_by, creation_date, last_modified_by, last_modified_date, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(12, NULL, NULL, NULL, NULL, true, '060.010.010.000', 'Capítulo de libro - BCI - Editorial nacional', 8, 'CAP_LIBRO_BCI _EDITORIAL_NACIONAL', 'CVN', 'PRINCIPAL', 4);
INSERT INTO test.configuracion_baremo
(id, created_by, creation_date, last_modified_by, last_modified_date, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(13, NULL, NULL, NULL, NULL, true, '060.010.010.000', 'Capítulo de libro - ICEE - Q1', 9, 'CAP_LIBRO_ICEE_Q1', 'CVN', 'PRINCIPAL', 4);
INSERT INTO test.configuracion_baremo
(id, created_by, creation_date, last_modified_by, last_modified_date, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(14, NULL, NULL, NULL, NULL, true, '060.010.010.000', 'Capítulo de libro -  ICEE - Resto cuartiles', 10, 'CAP_LIBRO_ICEE_RESTO_CUARTILES', 'CVN', 'PRINCIPAL', 4);
INSERT INTO test.configuracion_baremo
(id, created_by, creation_date, last_modified_by, last_modified_date, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(15, NULL, NULL, NULL, NULL, true, '060.010.010.000', 'Capítulo de libro - DIALNET', 11, 'CAP_LIBRO_DIALNET', 'CVN', 'PRINCIPAL', 4);
INSERT INTO test.configuracion_baremo
(id, created_by, creation_date, last_modified_by, last_modified_date, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(16, NULL, NULL, NULL, NULL, true, '060.010.010.000', 'Capítulo de libro - Otras contribuciones', 12, 'CAP_LIBRO_OTRAS', 'CVN', 'PRINCIPAL', 4);
INSERT INTO test.configuracion_baremo
(id, created_by, creation_date, last_modified_by, last_modified_date, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(17, NULL, NULL, NULL, NULL, true, '060.010.010.000', 'Edición - BCI - Editorial extranjera', 13, 'EDICION_BCI _EDITORIAL_EXTRANJERA', 'CVN', 'PRINCIPAL', 4);
INSERT INTO test.configuracion_baremo
(id, created_by, creation_date, last_modified_by, last_modified_date, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(18, NULL, NULL, NULL, NULL, true, '060.010.010.000', 'Edición - BCI - Editorial nacional', 14, 'EDICION_BCI_EDITORIAL_NACIONAL', 'CVN', 'PRINCIPAL', 4);
INSERT INTO test.configuracion_baremo
(id, created_by, creation_date, last_modified_by, last_modified_date, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(19, NULL, NULL, NULL, NULL, true, '060.010.010.000', 'Edición - ICEE - Q1', 15, 'EDICION_ICEE_Q1', 'CVN', 'PRINCIPAL', 4);
INSERT INTO test.configuracion_baremo
(id, created_by, creation_date, last_modified_by, last_modified_date, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(20, NULL, NULL, NULL, NULL, true, '060.010.010.000', 'Edición -  ICEE - Resto cuartiles', 16, 'EDICION_ICEE_RESTO_CUARTILES', 'CVN', 'PRINCIPAL', 4);
INSERT INTO test.configuracion_baremo
(id, created_by, creation_date, last_modified_by, last_modified_date, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(21, NULL, NULL, NULL, NULL, true, '060.010.010.000', 'Edición - DIALNET', 17, 'EDICION_DIALNET', 'CVN', 'PRINCIPAL', 4);
INSERT INTO test.configuracion_baremo
(id, created_by, creation_date, last_modified_by, last_modified_date, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(22, NULL, NULL, NULL, NULL, true, '060.010.010.000', 'Edición - Otras contribuciones', 18, 'EDICION_OTRAS', 'CVN', 'PRINCIPAL', 4);
INSERT INTO test.configuracion_baremo
(id, created_by, creation_date, last_modified_by, last_modified_date, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(23, NULL, NULL, NULL, NULL, true, '060.010.010.000', 'Comentario sistemático a normas - BCI - Editorial extranjera', 19, 'COMENTARIO_BCI _EDITORIAL_EXTRANJERA', 'CVN', 'PRINCIPAL', 4);
INSERT INTO test.configuracion_baremo
(id, created_by, creation_date, last_modified_by, last_modified_date, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(24, NULL, NULL, NULL, NULL, true, '060.010.010.000', 'Comentario sistemático a normas - BCI - Editorial nacional', 20, 'COMENTARIO_BCI _EDITORIAL_NACIONAL', 'CVN', 'PRINCIPAL', 4);
INSERT INTO test.configuracion_baremo
(id, created_by, creation_date, last_modified_by, last_modified_date, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(25, NULL, NULL, NULL, NULL, true, '060.010.010.000', 'Comentario sistemático a normas - ICEE - Q1', 21, 'COMENTARIO_ICEE_Q1', 'CVN', 'PRINCIPAL', 4);
INSERT INTO test.configuracion_baremo
(id, created_by, creation_date, last_modified_by, last_modified_date, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(26, NULL, NULL, NULL, NULL, true, '060.010.010.000', 'Comentario sistemático a normas -  ICEE - Resto cuartiles', 22, 'COMENTARIO_ICEE_RESTO_CUARTILES', 'CVN', 'PRINCIPAL', 4);
INSERT INTO test.configuracion_baremo
(id, created_by, creation_date, last_modified_by, last_modified_date, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(27, NULL, NULL, NULL, NULL, true, '060.010.010.000', 'Comentario sistemático a normas - DIALNET', 23, 'COMENTARIO_DIALNET', 'CVN', 'PRINCIPAL', 4);
INSERT INTO test.configuracion_baremo
(id, created_by, creation_date, last_modified_by, last_modified_date, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(28, NULL, NULL, NULL, NULL, true, '060.010.010.000', 'Comentario sistemático a normas - Otras contribuciones', 24, 'COMENTARIO_OTRAS', 'CVN', 'PRINCIPAL', 4);
INSERT INTO test.configuracion_baremo
(id, created_by, creation_date, last_modified_by, last_modified_date, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(29, NULL, NULL, NULL, NULL, true, '060.010.010.000', 'Número de autores', 1, 'LIBRO_NUMERO_AUTORES', 'CVN', 'MODULADOR', 4);
INSERT INTO test.configuracion_baremo
(id, created_by, creation_date, last_modified_by, last_modified_date, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(30, NULL, NULL, NULL, NULL, true, '060.010.010.000', 'Editorial de reconocido prestigio', 1, 'LIBRO_EDITORIAL_PRESTIGIO', 'CVN', 'EXTRA', 4);
