-- ROL PROYECTO
INSERT INTO test.rol_proyecto (id, abreviatura, rol_principal, orden, equipo, activo) VALUES(1, '001', false, null, 'INVESTIGACION', true);

INSERT INTO test.rol_proyecto_nombre(rol_proyecto_id, lang, value_) VALUES(1, 'es', 'nombre-001');

INSERT INTO test.rol_proyecto_descripcion(rol_proyecto_id, lang, value_) VALUES(1, 'es', 'descripcion-001');
