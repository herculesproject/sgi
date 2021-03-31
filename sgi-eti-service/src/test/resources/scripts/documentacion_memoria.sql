-- DEPENDENCIAS: DOCUMENTACION_MEMORIA
/*
  scripts = { 
    "classpath:scripts/memoria.sql",
    "classpath:scripts/tipo_documento.sql"
  }
*/

-- DOCUMENTACIÓN MEMORIA
INSERT INTO eti.documentacion_memoria (id, memoria_id, tipo_documento_id, documento_ref, aportado) VALUES (1, 2, 1, 'doc-001', true);
INSERT INTO eti.documentacion_memoria (id, memoria_id, tipo_documento_id, documento_ref, aportado) VALUES (2, 2, 2, 'doc-002', true);
INSERT INTO eti.documentacion_memoria (id, memoria_id, tipo_documento_id, documento_ref, aportado) VALUES (3, 2, 3, 'doc-003', true);
INSERT INTO eti.documentacion_memoria (id, memoria_id, tipo_documento_id, documento_ref, aportado) VALUES (4, 2, 4, 'doc-004', true);
INSERT INTO eti.documentacion_memoria (id, memoria_id, tipo_documento_id, documento_ref, aportado) VALUES (5, 2, 5, 'doc-005', true);
INSERT INTO eti.documentacion_memoria (id, memoria_id, tipo_documento_id, documento_ref, aportado) VALUES (6, 2, 6, 'doc-006', true);
INSERT INTO eti.documentacion_memoria (id, memoria_id, tipo_documento_id, documento_ref, aportado) VALUES (7, 2, 7, 'doc-007', true);
INSERT INTO eti.documentacion_memoria (id, memoria_id, tipo_documento_id, documento_ref, aportado) VALUES (8, 2, 3, 'doc-008', true);
INSERT INTO eti.documentacion_memoria (id, memoria_id, tipo_documento_id, documento_ref, aportado) VALUES (9, 3, 4, 'doc-009', true);
INSERT INTO eti.documentacion_memoria (id, memoria_id, tipo_documento_id, documento_ref, aportado) VALUES (10, 15, 1, 'doc-010', true);
INSERT INTO eti.documentacion_memoria (id, memoria_id, tipo_documento_id, documento_ref, aportado) VALUES (11, 16, 2, 'doc-011', true);
INSERT INTO eti.documentacion_memoria (id, memoria_id, tipo_documento_id, documento_ref, aportado) VALUES (12, 2, 3, 'doc-012', true);

ALTER SEQUENCE eti.documentacion_memoria_seq RESTART WITH 13;