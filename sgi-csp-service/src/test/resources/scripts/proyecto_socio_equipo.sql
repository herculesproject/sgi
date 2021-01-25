-- DEPENDENCIAS:  proyecto socio
/*
  scripts = { 

    "classpath:scripts/proyecto_socio.sql"
  }
*/

-- PROYECTO SOCIO EQUIPO
INSERT INTO csp.proyecto_socio_equipo (id, proyecto_socio_id, rol_proyecto_id, persona_ref,  fecha_inicio, fecha_fin)
  VALUES (1, 1, 1,'personaRef-1', '2021-01-11', '2022-01-11');
INSERT INTO csp.proyecto_socio_equipo (id, proyecto_socio_id, rol_proyecto_id,persona_ref,  fecha_inicio, fecha_fin)
  VALUES (2, 1, 1,'personaRef-2', '2021-01-11', '2022-01-11');
INSERT INTO csp.proyecto_socio_equipo (id, proyecto_socio_id, rol_proyecto_id,persona_ref,  fecha_inicio, fecha_fin)
  VALUES (3, 1, 1,'personaRef-3', '2021-01-11', '2022-01-11');