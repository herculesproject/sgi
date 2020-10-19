
-- CONVOCATORIA
INSERT INTO csp.convocatoria (id, codigo, activo) VALUES (1, 'codigo-001', true);

-- PLAN
INSERT INTO csp.plan (id, nombre, descripcion, activo) VALUES (1, 'nombre-001', 'descripcion-001', true);

-- PROGRAMA
INSERT INTO csp.programa (id, nombre, descripcion, plan_id, programa_padre_id, activo) VALUES (1, 'nombre-001', 'descripcion-001', 1L, null, true);
