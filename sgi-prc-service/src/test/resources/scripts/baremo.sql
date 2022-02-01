-- BAREMO
-- Sexenios
INSERT INTO test.baremo (id, configuracion_baremo_id, convocatoria_baremacion_id, peso, puntos, puntuacion_maxima, cuantia, tipo_cuantia) VALUES(1, 1, 1, 10, 15.00, NULL, NULL, NULL);
-- Aportación costes indirectos
INSERT INTO test.baremo (id, configuracion_baremo_id, convocatoria_baremacion_id, peso, puntos, puntuacion_maxima, cuantia, tipo_cuantia) VALUES(2, 2, 1, 15, 1.00, NULL, 1000, 'CUANTIA');
-- Producción científica
INSERT INTO test.baremo (id, configuracion_baremo_id, convocatoria_baremacion_id, peso, puntos, puntuacion_maxima, cuantia, tipo_cuantia) VALUES(3, 3, 1, 75, NULL, NULL, NULL, NULL);
-- Autoría - BCI - Editorial extranjera
INSERT INTO test.baremo (id, configuracion_baremo_id, convocatoria_baremacion_id, peso, puntos, puntuacion_maxima, cuantia, tipo_cuantia) VALUES(4, 4, NULL, 1, NULL, NULL, NULL, NULL);
