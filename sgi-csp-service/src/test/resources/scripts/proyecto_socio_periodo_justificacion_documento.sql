-- DEPENDENCIAS: rol socio, proyecto
/*
  scripts = { 
    "classpath:scripts/proyecto_socio_periodo_justificacion.sql",
     "classpath:scripts/tipo_documento.sql"
  }
*/


-- SOCIO PERIODO JUSTIFICACION DOCUMENTO
INSERT INTO test.proyecto_socio_periodo_justificacion_documento (id, proyecto_socio_periodo_justificacion_id,  documento_ref, tipo_documento_id, comentario, visible)
  VALUES (1, 1, 'doc-001', 1, 'comentario-1',  true);
  INSERT INTO test.proyecto_socio_periodo_justificacion_documento (id, proyecto_socio_periodo_justificacion_id,  documento_ref, tipo_documento_id, comentario, visible)
  VALUES (2, 1, 'doc-001', 1, 'comentario-1', true);
  INSERT INTO test.proyecto_socio_periodo_justificacion_documento (id, proyecto_socio_periodo_justificacion_id,  documento_ref, tipo_documento_id, comentario, visible)
  VALUES (3, 1, 'doc-001', 1, 'comentario-1', true);

-- SOCIO PERIODO JUSTIFICACION DOCUMENTO NOMBRE
INSERT INTO test.proyecto_socio_periodo_justificacion_documento_nombre (proyecto_socio_periodo_justificacion_documento_id, lang, value_)
  VALUES (1, 'es', 'nombre-1');
INSERT INTO test.proyecto_socio_periodo_justificacion_documento_nombre (proyecto_socio_periodo_justificacion_documento_id, lang, value_)
  VALUES (2, 'es', 'nombre-2');
INSERT INTO test.proyecto_socio_periodo_justificacion_documento_nombre (proyecto_socio_periodo_justificacion_documento_id, lang, value_)
  VALUES (3, 'es', 'nombre-3');
