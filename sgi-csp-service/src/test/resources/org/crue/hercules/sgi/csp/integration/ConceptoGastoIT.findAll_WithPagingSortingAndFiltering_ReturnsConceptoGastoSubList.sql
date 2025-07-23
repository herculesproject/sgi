-- CONCEPTO_GASTO
INSERT INTO test.concepto_gasto (id, descripcion, costes_indirectos, activo) VALUES (1, 'descripcion-001', true,  true);
INSERT INTO test.concepto_gasto (id, descripcion, costes_indirectos, activo) VALUES (2, 'descripcion-002', true,  true);
INSERT INTO test.concepto_gasto (id, descripcion, costes_indirectos, activo) VALUES (3, 'descripcion-003', true, true);
INSERT INTO test.concepto_gasto (id, descripcion, costes_indirectos, activo) VALUES (4, 'descripcion-4', true,  false);
INSERT INTO test.concepto_gasto (id, descripcion, costes_indirectos, activo) VALUES (5, 'descripcion-5', true,  true);
INSERT INTO test.concepto_gasto (id, descripcion, costes_indirectos, activo) VALUES (6, 'descripcion-6', true,  true);
INSERT INTO test.concepto_gasto (id, descripcion, costes_indirectos, activo) VALUES (7, 'descripcion-7', true,  true);

-- CONCEPTO_GASTO_NOMBRE
INSERT INTO test.concepto_gasto_nombre (concepto_gasto_id, lang, value_) VALUES (1, 'es', 'nombre-001');
INSERT INTO test.concepto_gasto_nombre (concepto_gasto_id, lang, value_) VALUES (2, 'es', 'nombre-002');
INSERT INTO test.concepto_gasto_nombre (concepto_gasto_id, lang, value_) VALUES (3, 'es', 'nombre-003');
INSERT INTO test.concepto_gasto_nombre (concepto_gasto_id, lang, value_) VALUES (4, 'es', 'nombre-4');
INSERT INTO test.concepto_gasto_nombre (concepto_gasto_id, lang, value_) VALUES (5, 'es', 'nombre-5');
INSERT INTO test.concepto_gasto_nombre (concepto_gasto_id, lang, value_) VALUES (6, 'es', 'nombre-6');
INSERT INTO test.concepto_gasto_nombre (concepto_gasto_id, lang, value_) VALUES (7, 'es', 'nombre-7');