-- CONCEPTO_GASTO
INSERT INTO test.concepto_gasto (id, descripcion, costes_indirectos, activo) VALUES (1, 'descripcion-001', true, true);
INSERT INTO test.concepto_gasto (id, descripcion, costes_indirectos, activo) VALUES (2, 'descripcion-002', true, true);

-- CONCEPTO_GASTO_NOMBRE
INSERT INTO test.concepto_gasto_nombre (concepto_gasto_id, lang, value_) VALUES (1, 'es', 'nombre-001');
INSERT INTO test.concepto_gasto_nombre (concepto_gasto_id, lang, value_) VALUES (2, 'es', 'nombre-002');