-- INVENCION
/*
  scripts = { 
    "classpath:scripts/tipo_proteccion.sql",
  }
*/
INSERT INTO test.invencion
(id, activo, comentarios, fecha_comunicacion, proyecto_ref, tipo_proteccion_id)
VALUES 
(1, TRUE,  'comentarios-invencion-001', '2020-01-12T00:00:00Z', null, 1),
(2, TRUE,  'comentarios-invencion-002', '2020-01-13T00:00:00Z', null, 1),
(3, TRUE,  'comentarios-invencion-003', '2020-01-14T00:00:00Z', null, 1),
(4, FALSE, 'comentarios-invencion-004', '2020-01-14T00:00:00Z', null, 1);

-- INVENCION TITULO
INSERT INTO test.invencion_titulo(invencion_id, lang, value_) 
VALUES
(1, 'es', 'titulo-invencion-001'),
(2, 'es', 'titulo-invencion-002'),
(3, 'es', 'titulo-invencion-003'),
(4, 'es', 'titulo-invencion-004');

-- INVENCION DESCRIPCION
INSERT INTO test.invencion_descripcion(invencion_id, lang, value_) 
VALUES
(1, 'es', 'descripcion-invencion-001'),
(2, 'es', 'descripcion-invencion-002'),
(3, 'es', 'descripcion-invencion-003'),
(4, 'es', 'descripcion-invencion-004');

ALTER SEQUENCE test.invencion_seq RESTART WITH 5;


