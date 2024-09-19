-- ROL PROYECTO
INSERT INTO test.rol_proyecto (id, abreviatura, descripcion, rol_principal, orden, equipo, activo) VALUES(1, '001', 'descripcion-001', false, null, 'INVESTIGACION', true);

INSERT INTO test.rol_proyecto_nombre(rol_proyecto_id, lang, value_) VALUES(1, 'es', 'nombre-001');
