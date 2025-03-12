-- RESULTADO INFORME PATENTABILIDAD
INSERT INTO test.resultado_informe_patentabilidad (id, activo, descripcion) 
VALUES 
(1, TRUE, 'descricpion-resultado-informe-001'),
(2, TRUE, 'descricpion-resultado-informe-002'),
(3, TRUE, 'descricpion-resultado-informe-003'),
(4, FALSE, 'descricpion-resultado-informe-004');

-- RESULTADO INFORME PATENTABILIDAD NOMBRE
INSERT INTO test.resultado_informe_patentabilidad_nombre(resultado_informe_patentabilidad_id, lang, value_) 
VALUES
(1, 'es', 'nombre-resultado-informe-001'),
(2, 'es', 'nombre-resultado-informe-002'),
(3, 'es', 'nombre-resultado-informe-003'),
(4, 'es', 'nombre-resultado-informe-004');

ALTER SEQUENCE test.resultado_informe_patentabilidad_seq RESTART WITH 5;
