-- DEPENDENCIAS:
/*

*/

INSERT INTO test.empresa
(id, activo, tipo_empresa, estado, fecha_solicitud, objeto_social, conocimiento_tecnologia)
VALUES
(1, true, 'EBT', 'EN_TRAMITACION', '2022-01-01 22:59:59.000', 'objetoSocial', 'conocimientoTecnologico'),
(2, true, 'EBT', 'EN_TRAMITACION', '2022-01-01 22:59:59.000', 'objetoSocial 1', 'conocimientoTecnologico 1'),
(3, true, 'EBT', 'EN_TRAMITACION', '2022-01-01 22:59:59.000', 'objetoSocial BIS', 'conocimientoTecnologico BIS');

-- EMPRESA NOMBRE RAZÓN SOCIAL
INSERT INTO test.empresa_nombre_razon_social(empresa_id, lang, value_) 
VALUES
(1, 'es', 'nombreRazonSocial 1'),
(2, 'es', 'nombreRazonSocial 2'),
(3, 'es', 'nombreRazonSocial 3');