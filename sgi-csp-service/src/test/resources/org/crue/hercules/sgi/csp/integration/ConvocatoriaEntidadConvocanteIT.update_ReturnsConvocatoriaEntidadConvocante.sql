
-- CONVOCATORIA
INSERT INTO csp.convocatoria (id, codigo, activo) VALUES (1, 'codigo-001', true);

-- PLAN
INSERT INTO csp.plan (id, nombre, descripcion, activo) VALUES (1, 'nombre-001', 'descripcion-001', true);

-- PROGRAMA
INSERT INTO csp.programa (id, nombre, descripcion, plan_id, programa_padre_id, activo) VALUES (1, 'nombre-001', 'descripcion-001', 1L, null, true);

-- CONVOCATORIA ENTIDAD CONVOCANTE
INSERT INTO csp.convocatoria_entidad_convocante (id,  convocatoria_id, entidad_ref, programa_id) VALUES (1, 1, 'entidad-1', 1);
INSERT INTO csp.convocatoria_entidad_convocante (id,  convocatoria_id, entidad_ref, programa_id) VALUES (2, 1, 'entidad-2', 1);
