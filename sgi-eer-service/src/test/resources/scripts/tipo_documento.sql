INSERT INTO test.tipo_documento
(id, descripcion, tipo_documento_padre_id)
VALUES
(1, 'Documentos de procedimiento', null),
(2, 'Documentos corporativos', null),
(3, 'Estutos sociales', null),
-- SUBTIPO DOCUMENTO 1 - PADRE_ID = 1
(4, 'Solicitud de creación', 1),
(5, 'Solicitud de incorporación', 1),
(6, 'Informe viabilidad de la OTRI', 1),
-- SUBTIPO DOCUMENTO 2 - PADRE_ID = 2
(7, 'Informe anual de cuentas', 2),
(8, 'Acta de reunión', 2),
-- SUBTIPO DOCUMENTO 3 - PADRE_ID = 3
(9, 'Modificación composición sociedad', 3);

--TIPO DOCUMENTO NOMBRE
INSERT INTO test.tipo_documento_nombre(tipo_documento_id, lang, value_) 
VALUES
(1, 'es', 'Documentos de procedimiento'),
(2, 'es', 'Documentos corporativos'),
(3, 'es', 'Estutos sociales'),
(4, 'es', 'Solicitud de creación'),
(5, 'es', 'Solicitud de incorporación'),
(6, 'es', 'Informe viabilidad de la OTRI'),
(7, 'es', 'Informe anual de cuentas'),
(8, 'es', 'Acta de reunión'),
(9, 'es', 'Modificación composición sociedad');