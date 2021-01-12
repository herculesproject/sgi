
-- MODELO EJECUCION
INSERT INTO csp.modelo_ejecucion (id, nombre, descripcion, activo) VALUES (1, 'nombre-1', 'descripcion-1', true);

-- MODELO UNIDAD
INSERT INTO csp.modelo_unidad (id, unidad_gestion_ref, modelo_ejecucion_id, activo) VALUES (1, 'OPE', 1, true);

-- TIPO_FINALIDAD
INSERT INTO csp.tipo_finalidad (id,nombre,descripcion,activo) VALUES (1,'nombre-1','descripcion-1',true);

-- TIPO AMBITO GEOGRAFICO
INSERT INTO csp.tipo_ambito_geografico (id, nombre, activo) VALUES (1, 'nombre-001', true);

-- ESTADO PROYECTO
INSERT INTO csp.estado_proyecto (id, id_proyecto, estado, fecha_estado, comentario) VALUES (1, 1, 'Borrador', '2020-11-17 15:00', 'comentario');

-- PROYECTO
INSERT INTO csp.proyecto (id, titulo, codigo_externo, fecha_inicio, fecha_fin, unidad_gestion_ref, modelo_ejecucion_id, tipo_finalidad_id, tipo_ambito_geografico_id, confidencial, estado_proyecto_id, activo)
 VALUES (1, 'PRO1', 'cod-externo-001', '2020-12-12', '2020-12-31', 'OPE', 1, 1, 1, false, 1, true);
 INSERT INTO csp.proyecto (id, titulo, codigo_externo, fecha_inicio, fecha_fin, unidad_gestion_ref, modelo_ejecucion_id, tipo_finalidad_id, tipo_ambito_geografico_id, confidencial, estado_proyecto_id, activo)
 VALUES (2, 'PRO2', 'cod-externo-002', '2020-12-12', '2020-12-31', 'OPE', 1, 1, 1, false, 1, true);