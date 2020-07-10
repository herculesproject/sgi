-- COMITE 
INSERT INTO eti.comite (id, comite, activo) VALUES (1, 'Comite1', true);

-- TIPO MEMORIA 
INSERT INTO eti.tipo_memoria (id, nombre, activo) VALUES (1, 'TipoMemoria1', true);

-- TIPO MEMORIA COMITE
INSERT INTO eti.tipo_memoria_comite (id, comite, tipo_memoria) VALUES (2, 1, 1);