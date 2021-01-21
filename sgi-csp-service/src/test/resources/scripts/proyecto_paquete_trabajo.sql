-- DEPENDENCIAS: proyecto 
/*
  scripts = { 
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/estado_proyecto.sql",    
    "classpath:scripts/proyecto.sql"
  }
*/

INSERT INTO csp.proyecto_paquete_trabajo
(id, descripcion, fecha_fin, fecha_inicio, nombre, persona_mes, proyecto_id)
VALUES(1, 'descripcion-proyecto-equipo-trabajo-001', '2020-01-15', '2020-01-01', 'proyecto-paquete-trabajo-001', 1, 1);

INSERT INTO csp.proyecto_paquete_trabajo
(id, descripcion, fecha_fin, fecha_inicio, nombre, persona_mes, proyecto_id)
VALUES(2, 'descripcion-proyecto-equipo-trabajo-002', '2020-02-15', '2020-02-01', 'proyecto-paquete-trabajo-002', 2, 1);

INSERT INTO csp.proyecto_paquete_trabajo
(id, descripcion, fecha_fin, fecha_inicio, nombre, persona_mes, proyecto_id)
VALUES(3, 'descripcion-proyecto-equipo-trabajo-003', '2020-03-15', '2020-03-01', 'proyecto-paquete-trabajo-003', 3, 1);

INSERT INTO csp.proyecto_paquete_trabajo
(id, descripcion, fecha_fin, fecha_inicio, nombre, persona_mes, proyecto_id)
VALUES(4, 'descripcion-proyecto-equipo-trabajo-004', '2020-04-15', '2020-04-01', 'proyecto-paquete-trabajo-4', 4, 1);

INSERT INTO csp.proyecto_paquete_trabajo
(id, descripcion, fecha_fin, fecha_inicio, nombre, persona_mes, proyecto_id)
VALUES(5, 'descripcion-proyecto-equipo-trabajo-005', '2020-05-15', '2020-05-01', 'proyecto-paquete-trabajo-5', 5, 1);
