-- CONFIGURACION con todos los campos informados (parámetros por clave)
INSERT INTO test.configuracion (id, dias_archivada_inactivo, meses_archivada_pendiente_correcciones, dias_limite_evaluador, dias_aviso_retrospectiva, duracion_proyecto_evaluacion, peticion_evaluacion_unidades_vinculacion_enabled, peticion_evaluacion_inicializar_unidad_vinculacion_enabled, tipo_unidad_vinculacion_departamento_ref)
VALUES (1, 6, 45, 3, 3, 4, false, false, null);

ALTER SEQUENCE test.configuracion_seq RESTART WITH 2;
