-- COMITE 
INSERT INTO eti.comite (id, comite, activo) VALUES (1, 'Comite1', true);

-- TIPO MEMORIA 
INSERT INTO eti.tipo_memoria (id, nombre, activo) VALUES (1, 'TipoMemoria1', true);

-- TIPO MEMORIA COMITE
INSERT INTO eti.tipo_memoria_comite (id, comite_id, tipo_memoria_id) VALUES (1, 1, 1);