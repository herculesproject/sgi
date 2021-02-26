-- DEPENDENCIAS: proyecto
/*
  scripts = { 
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",    
    "classpath:scripts/proyecto.sql"
  }
*/
INSERT INTO csp.estado_proyecto 
(id, id_proyecto, estado, fecha_estado, comentario) 
VALUES (1, 1, 'BORRADOR', '2020-11-17 15:00', 'comentario-estado-proyecto-001');



