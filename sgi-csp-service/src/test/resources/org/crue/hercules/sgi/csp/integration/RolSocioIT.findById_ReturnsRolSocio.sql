-- ROL SOCIO
INSERT INTO test.rol_socio (id, abreviatura, coordinador, activo) VALUES(1, '001', false, true);

INSERT INTO test.rol_socio_nombre(rol_socio_id, lang, value_) VALUES(1, 'es', 'nombre-001');

INSERT INTO test.rol_socio_descripcion (rol_socio_id, lang, value_) VALUES(1, 'es', 'descripcion-001');
