
-- PLAN
INSERT INTO csp.plan (id, nombre, descripcion, activo) VALUES (1, 'nombre-001', 'descripcion-001', true);
INSERT INTO csp.plan (id, nombre, descripcion, activo) VALUES (2, 'nombre-002', 'descripcion-002', true);

-- PROGRAMA
INSERT INTO csp.programa (id, nombre, descripcion, plan_id, programa_padre_id, activo) VALUES (1, 'nombre-001', 'descripcion-001', 1L, null, true);
INSERT INTO csp.programa (id, nombre, descripcion, plan_id, programa_padre_id, activo) VALUES (2, 'nombre-002', 'descripcion-002', 1L, 1L, true);
INSERT INTO csp.programa (id, nombre, descripcion, plan_id, programa_padre_id, activo) VALUES (3, 'nombre-003', 'descripcion-003', 1L, 1L, false);
INSERT INTO csp.programa (id, nombre, descripcion, plan_id, programa_padre_id, activo) VALUES (11, 'nombre-011', 'descripcion-011', 1L, null, true);
INSERT INTO csp.programa (id, nombre, descripcion, plan_id, programa_padre_id, activo) VALUES (12, 'nombre-012', 'descripcion-012', 1L, 11, true);
INSERT INTO csp.programa (id, nombre, descripcion, plan_id, programa_padre_id, activo) VALUES (13, 'nombre-013', 'descripcion-013', 1L, 12, true);


