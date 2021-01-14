-- DEPENDENCIAS: modelo_ejecucion
/*
  scripts = { 
    "classpath:scripts/modelo_ejecucion.sql",
  }
*/

INSERT INTO csp.modelo_unidad 
(id, unidad_gestion_ref, modelo_ejecucion_id, activo) 
VALUES (1, 'OPE', 1, true);

INSERT INTO csp.modelo_unidad 
(id, unidad_gestion_ref, modelo_ejecucion_id, activo) 
VALUES (2, 'OTRI', 1, true);


