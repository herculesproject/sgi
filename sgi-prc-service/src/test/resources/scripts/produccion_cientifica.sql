-- PRODUCCION_CIENTIFICA
INSERT INTO test.produccion_cientifica (id, epigrafe_cvn, produccion_cientifica_ref, estado_produccion_cientifica_id) VALUES(1, '060.010.010.000',  'produccion-cientifica-ref-001',  NULL);
INSERT INTO test.produccion_cientifica (id, epigrafe_cvn, produccion_cientifica_ref, estado_produccion_cientifica_id) VALUES(2, '060.010.010.000',  'produccion-cientifica-ref-002',  NULL);
INSERT INTO test.produccion_cientifica (id, epigrafe_cvn, produccion_cientifica_ref, estado_produccion_cientifica_id) VALUES(3, '060.010.020.000',  'produccion-cientifica-ref-003',  NULL);

INSERT INTO test.estado_produccion_cientifica (id, comentario, estado, fecha, produccion_cientifica_id) VALUES(1, NULL, 'VALIDADO', '2022-01-20T23:59:59Z', 1);
INSERT INTO test.estado_produccion_cientifica (id, comentario, estado, fecha, produccion_cientifica_id) VALUES(2, NULL, 'PENDIENTE', '2022-01-20T23:59:59Z', 2);
INSERT INTO test.estado_produccion_cientifica (id, comentario, estado, fecha, produccion_cientifica_id) VALUES(3, NULL, 'PENDIENTE', '2022-01-20T23:59:59Z', 3);

update test.produccion_cientifica set estado_produccion_cientifica_id = 1 where id=1;
update test.produccion_cientifica set estado_produccion_cientifica_id = 2 where id=2;
update test.produccion_cientifica set estado_produccion_cientifica_id = 3 where id=3;
