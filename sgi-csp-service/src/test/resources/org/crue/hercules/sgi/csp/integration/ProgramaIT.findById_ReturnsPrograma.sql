-- PLAN
INSERT INTO csp.plan (id, nombre, descripcion, activo) VALUES (1, 'nombre-001', 'descripcion-001', true);
INSERT INTO csp.plan (id, nombre, descripcion, activo) VALUES (2, 'nombre-002', 'descripcion-002', true);

-- PROGRAMA
INSERT INTO csp.programa (id, nombre, descripcion, plan_id, programa_padre_id, activo) VALUES (1, 'nombre-001', 'descripcion-001', 1L, null, true);
INSERT INTO csp.programa (id, nombre, descripcion, plan_id, programa_padre_id, activo) VALUES (2, 'nombre-002', 'descripcion-002', 1L, 1L, true);
