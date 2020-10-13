
-- CONVOCATORIA
INSERT INTO csp.convocatoria (id, codigo, activo) VALUES (1, 'codigo-001', true);
INSERT INTO csp.convocatoria (id, codigo, activo) VALUES (2, 'codigo-002', true);

-- CONVOCATORIA ENTIDAD GESTORA
INSERT INTO csp.convocatoria_entidad_gestora (id,  convocatoria_id, entidad_ref) VALUES (1, 1, 'entidad-001');
INSERT INTO csp.convocatoria_entidad_gestora (id,  convocatoria_id, entidad_ref) VALUES (2, 1, 'entidad-002');
INSERT INTO csp.convocatoria_entidad_gestora (id,  convocatoria_id, entidad_ref) VALUES (3, 1, 'entidad-003');
INSERT INTO csp.convocatoria_entidad_gestora (id,  convocatoria_id, entidad_ref) VALUES (4, 1, 'entidad-4');
INSERT INTO csp.convocatoria_entidad_gestora (id,  convocatoria_id, entidad_ref) VALUES (5, 2, 'entidad-001');
INSERT INTO csp.convocatoria_entidad_gestora (id,  convocatoria_id, entidad_ref) VALUES (6, 2, 'entidad-002');
INSERT INTO csp.convocatoria_entidad_gestora (id,  convocatoria_id, entidad_ref) VALUES (7, 2, 'entidad-003');
INSERT INTO csp.convocatoria_entidad_gestora (id,  convocatoria_id, entidad_ref) VALUES (8, 2, 'entidad-004');