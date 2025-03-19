-- DEPENDENCIAS:
/*

*/

INSERT INTO test.empresa
(id, activo, tipo_empresa, estado, fecha_solicitud, conocimiento_tecnologia)
VALUES
(1, true, 'EBT', 'EN_TRAMITACION', '2022-01-01 22:59:59.000', 'conocimientoTecnologico'),
(2, true, 'EBT', 'EN_TRAMITACION', '2022-01-01 22:59:59.000', 'conocimientoTecnologico 1'),
(3, true, 'EBT', 'EN_TRAMITACION', '2022-01-01 22:59:59.000', 'conocimientoTecnologico BIS');

-- EMPRESA NOMBRE RAZÓN SOCIAL
INSERT INTO test.empresa_nombre_razon_social(empresa_id, lang, value_) 
VALUES
(1, 'es', 'nombreRazonSocial 1'),
(2, 'es', 'nombreRazonSocial 2'),
(3, 'es', 'nombreRazonSocial 3');

-- EMPRESA OBJETO SOCIAL
INSERT INTO test.empresa_objeto_social(empresa_id, lang, value_) 
VALUES
(1, 'es', 'objetoSocial'),
(2, 'es', 'objetoSocial 1'),
(3, 'es', 'objetoSocial BIS');