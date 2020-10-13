
-- CONVOCATORIA
INSERT INTO csp.convocatoria (id, codigo, activo) VALUES (1, 'codigo-001', true);

-- CONVOCATORIA ENTIDAD GESTORA
INSERT INTO csp.convocatoria_entidad_gestora (id,  convocatoria_id, entidad_ref) VALUES (1, 1, 'entidad-001');
INSERT INTO csp.convocatoria_entidad_gestora (id,  convocatoria_id, entidad_ref) VALUES (2, 1, 'entidad-002');