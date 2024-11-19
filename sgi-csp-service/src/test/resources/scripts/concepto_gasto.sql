-- CONCEPTO_GASTO
INSERT INTO test.concepto_gasto (id, descripcion, costes_indirectos, activo) VALUES (1, 'descripcion-001', true,  true);
INSERT INTO test.concepto_gasto (id, descripcion, costes_indirectos, activo) VALUES (2, 'descripcion-002', true,  true);
INSERT INTO test.concepto_gasto (id, descripcion, costes_indirectos, activo) VALUES (3, 'descripcion-003', false, false);
INSERT INTO test.concepto_gasto (id, descripcion, costes_indirectos, activo) VALUES (11, 'descripcion-011', true,  true);
INSERT INTO test.concepto_gasto (id, descripcion, costes_indirectos, activo) VALUES (12, 'descripcion-012', true,  true);
INSERT INTO test.concepto_gasto (id, descripcion, costes_indirectos, activo) VALUES (13, 'descripcion-013', true,  true);

-- CONCEPTO_GASTO_NOMBRE
INSERT INTO test.concepto_gasto_nombre (concepto_gasto_id, lang, value_) VALUES (1, 'es', 'nombre-001');
INSERT INTO test.concepto_gasto_nombre (concepto_gasto_id, lang, value_) VALUES (2, 'es', 'nombre-002');
INSERT INTO test.concepto_gasto_nombre (concepto_gasto_id, lang, value_) VALUES (3, 'es', 'nombre-003');
INSERT INTO test.concepto_gasto_nombre (concepto_gasto_id, lang, value_) VALUES (11, 'es', 'nombre-011');
INSERT INTO test.concepto_gasto_nombre (concepto_gasto_id, lang, value_) VALUES (12, 'es', 'nombre-012');
INSERT INTO test.concepto_gasto_nombre (concepto_gasto_id, lang, value_) VALUES (13, 'es', 'nombre-013');