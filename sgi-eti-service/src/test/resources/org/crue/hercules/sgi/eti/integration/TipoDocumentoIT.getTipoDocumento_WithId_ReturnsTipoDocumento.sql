-- COMITÉ
INSERT INTO eti.comite (id, comite, activo) VALUES (1, 'Comite1', true);

-- TIPO DOCUMENTO 
INSERT INTO eti.tipo_documento (id, nombre, comite_id, activo) VALUES (1, 'TipoDocumento1', 1, true);
INSERT INTO eti.tipo_documento (id, nombre, comite_id, activo) VALUES (2, 'TipoDocumento2', 1, true);
