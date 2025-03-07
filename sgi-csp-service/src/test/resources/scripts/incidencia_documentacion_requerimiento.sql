-- DEPENDENCIAS: requerimiento_justificacion

/*
  scripts = { 
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/proyecto.sql",
    "classpath:scripts/proyecto_proyecto_sge.sql",
    "classpath:scripts/proyecto_periodo_justificacion.sql",
    "classpath:scripts/tipo_requerimiento.sql",
    "classpath:scripts/requerimiento_justificacion.sql"
  }
*/

INSERT INTO test.incidencia_documentacion_requerimiento (id, requerimiento_justificacion_id, incidencia, alegacion)
VALUES(1, 1, 'incidencia-001', 'alegacion-001');
INSERT INTO test.incidencia_documentacion_requerimiento (id, requerimiento_justificacion_id, incidencia, alegacion)
VALUES(2, 1, 'incidencia-002', 'alegacion-002');
INSERT INTO test.incidencia_documentacion_requerimiento (id, requerimiento_justificacion_id, incidencia, alegacion)
VALUES(3, 1, 'incidencia-003', 'alegacion-003');
INSERT INTO test.incidencia_documentacion_requerimiento (id, requerimiento_justificacion_id, incidencia, alegacion)
VALUES(4, 2, 'incidencia-001', 'alegacion-001');

-- INCIDENCIA_DOCUMENTACION_REQUERIMIENTO_NOMBRE_DOCUMENTO
INSERT INTO test.incidencia_documentacion_requerimiento_nombre_documento (incidencia_documentacion_requerimiento_id, lang, value_) VALUES (1,  'es', 'nombre-documento-001');
INSERT INTO test.incidencia_documentacion_requerimiento_nombre_documento (incidencia_documentacion_requerimiento_id, lang, value_) VALUES (2,  'es', 'nombre-documento-002');
INSERT INTO test.incidencia_documentacion_requerimiento_nombre_documento (incidencia_documentacion_requerimiento_id, lang, value_) VALUES (3,  'es', 'nombre-documento-003');
INSERT INTO test.incidencia_documentacion_requerimiento_nombre_documento (incidencia_documentacion_requerimiento_id, lang, value_) VALUES (4,  'es', 'nombre-documento-001');