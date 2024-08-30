-- DEPENDENCIAS: MEMORIA
/*
  scripts = { 
  "classpath:scripts/peticion_evaluacion.sql",
  "classpath:scripts/comite.sql", 
  "classpath:scripts/tipo_estado_memoria.sql",
  "classpath:scripts/retrospectiva.sql" 
  }
*/

-- MEMORIA 
INSERT INTO test.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, formulario_id, formulario_seguimiento_anual_id, formulario_seguimiento_final_id, formulario_retrospectiva_id, titulo, persona_ref, tipo, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, activo, memoria_original_id)
 VALUES 
 ( 2, 'ref-002', 2, 1, 1, 4, 5, null, 'Memoria002', 'userref-002', 'NUEVA', 12, '2020-08-01T00:00:00Z', false, null, 7, true, null),
 ( 3, 'ref-003', 2, 1, 1, 4, 5, null, 'Memoria003', 'userref-003', 'NUEVA',  1,                   null, false, null, 1, true, null),
 ( 4, 'ref-004', 2, 1, 1, 4, 5, null, 'Memoria004', 'userref-004', 'NUEVA', 17, '2020-08-01T00:00:00Z', false, null, 1, true, null),
 ( 5, 'ref-005', 2, 1, 1, 4, 5, null, 'Memoria005', 'userref-005', 'NUEVA',  1,                   null, false, null, 1, true, null),
 ( 6, 'ref-006', 2, 1, 1, 4, 5, null, 'Memoria006', 'userref-006', 'NUEVA', 12, '2020-08-01T00:00:00Z', false, null, 1, true, null),
 ( 7, 'ref-007', 2, 1, 1, 4, 5, null, 'Memoria007', 'userref-007', 'NUEVA', 11,                   null, false, null, 1, true, null),
 ( 8, 'ref-008', 2, 1, 1, 4, 5, null, 'Memoria008', 'userref-008', 'NUEVA',  1,                   null, false, null, 1, true, null),
 ( 9, 'ref-009', 2, 2, 2, 4, 5,    6, 'Memoria009', 'userref-009', 'NUEVA',  2,                   null,  true,    8, 1, true, null),
 (10, 'ref-010', 2, 1, 1, 4, 5, null, 'Memoria010', 'userref-010', 'NUEVA', 19, '2020-08-01T00:00:00Z', false, null, 1, true, null),
 (11, 'ref-011', 2, 1, 1, 4, 5, null, 'Memoria011', 'userref-011', 'NUEVA',  3, '2020-08-01T00:00:00Z', false, null, 1, true, null),
 (12, 'ref-012', 2, 1, 1, 4, 5, null, 'Memoria012', 'userref-012', 'NUEVA',  3, '2020-08-01T00:00:00Z', false, null, 1, true, null),
 (13, 'ref-013', 2, 1, 1, 4, 5, null, 'Memoria013', 'userref-013', 'NUEVA',  1, '2020-08-01T00:00:00Z', false, null, 1, true, null),
 (14, 'ref-014', 2, 1, 1, 4, 5, null, 'Memoria014', 'userref-014', 'NUEVA', 13, '2020-09-01T00:00:00Z', false, null, 1, true, null),
 (15, 'ref-015', 2, 1, 1, 4, 5, null, 'Memoria015', 'userref-015', 'NUEVA',  9,                   null, false, null, 1, true, null),
 (16, 'ref-016', 2, 2, 2, 4, 5,    6, 'Memoria016', 'userref-016', 'NUEVA', 14,                   null,  true,    4, 1, true, null),
 (17, 'ref-017', 2, 2, 2, 4, 5,    6, 'Memoria017', 'userref-017', 'NUEVA', 13,                   null,  true,    4, 1, true, null);

ALTER SEQUENCE test.memoria_seq RESTART WITH 18;