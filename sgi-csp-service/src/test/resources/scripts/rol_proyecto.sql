-- ROL PROYECTO
INSERT INTO test.rol_proyecto 
(id, abreviatura, rol_principal, orden, equipo, activo, baremable_prc) 
VALUES
(1, '001', true, 'PRIMARIO',   'INVESTIGACION', true,  true),
(2, '002', true, 'SECUNDARIO', 'INVESTIGACION', true,  true),
(3, '003', false, null,        'INVESTIGACION', true,  true),
(4, '004', false, null,        'INVESTIGACION', true,  true),
(5, '005', false, null,        'INVESTIGACION', true,  true),
(6, '006', false, null,        'INVESTIGACION', true,  true),
(7, '007', false, null,        'INVESTIGACION', false, true),
(8, '008', false, null,        'INVESTIGACION', false, true);

--ROL PROYECTO NOMBRE
INSERT INTO test.rol_proyecto_nombre(rol_proyecto_id, lang, value_) VALUES(1, 'es', 'nombre-001');
INSERT INTO test.rol_proyecto_nombre(rol_proyecto_id, lang, value_) VALUES(2, 'es', 'nombre-002');
INSERT INTO test.rol_proyecto_nombre(rol_proyecto_id, lang, value_) VALUES(3, 'es', 'nombre-003');
INSERT INTO test.rol_proyecto_nombre(rol_proyecto_id, lang, value_) VALUES(4, 'es', 'nombre-004');
INSERT INTO test.rol_proyecto_nombre(rol_proyecto_id, lang, value_) VALUES(5, 'es', 'nombre-005');
INSERT INTO test.rol_proyecto_nombre(rol_proyecto_id, lang, value_) VALUES(6, 'es', 'nombre-006');
INSERT INTO test.rol_proyecto_nombre(rol_proyecto_id, lang, value_) VALUES(7, 'es', 'nombre-007');
INSERT INTO test.rol_proyecto_nombre(rol_proyecto_id, lang, value_) VALUES(8, 'es', 'nombre-008');

--ROL PROYECTO DESCRIPCION
INSERT INTO test.rol_proyecto_descripcion(rol_proyecto_id, lang, value_) VALUES(1, 'es', 'descripcion-001');
INSERT INTO test.rol_proyecto_descripcion(rol_proyecto_id, lang, value_) VALUES(2, 'es', 'descripcion-002');
INSERT INTO test.rol_proyecto_descripcion(rol_proyecto_id, lang, value_) VALUES(3, 'es', 'descripcion-003');
INSERT INTO test.rol_proyecto_descripcion(rol_proyecto_id, lang, value_) VALUES(4, 'es', 'descripcion-4');
INSERT INTO test.rol_proyecto_descripcion(rol_proyecto_id, lang, value_) VALUES(5, 'es', 'descripcion-5');
INSERT INTO test.rol_proyecto_descripcion(rol_proyecto_id, lang, value_) VALUES(6, 'es', 'descripcion-6');
INSERT INTO test.rol_proyecto_descripcion(rol_proyecto_id, lang, value_) VALUES(7, 'es', 'descripcion-007');
INSERT INTO test.rol_proyecto_descripcion(rol_proyecto_id, lang, value_) VALUES(8, 'es', 'descripcion-008');