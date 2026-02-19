-- vacia la bd y reinicia secuencias
DELETE FROM test.autor_grupo;
ALTER SEQUENCE test.autor_grupo_seq RESTART WITH 1;

DELETE FROM test.autor;
ALTER SEQUENCE test.autor_seq RESTART WITH 1;

UPDATE test.produccion_cientifica SET estado_produccion_cientifica_id = null;
DELETE FROM test.estado_produccion_cientifica;
ALTER SEQUENCE test.estado_produccion_cientifica_seq RESTART WITH 1;

DELETE FROM test.proyecto;
ALTER SEQUENCE test.proyecto_seq RESTART WITH 1;

DELETE FROM test.acreditacion;
ALTER SEQUENCE test.acreditacion_seq RESTART WITH 1;

DELETE FROM test.indice_impacto;
ALTER SEQUENCE test.indice_impacto_seq RESTART WITH 1;

DELETE FROM test.valor_campo;
ALTER SEQUENCE test.valor_campo_seq RESTART WITH 1;

DELETE FROM test.campo_produccion_cientifica;
ALTER SEQUENCE test.campo_produccion_cientifica_seq RESTART WITH 1;

UPDATE test.puntuacion_grupo_investigador SET puntuacion_item_investigador_id = null;
DELETE FROM test.puntuacion_item_investigador;
ALTER SEQUENCE test.puntuacion_item_investigador_seq RESTART WITH 1;

DELETE FROM test.puntuacion_baremo_item;
ALTER SEQUENCE test.puntuacion_baremo_item_seq RESTART WITH 1;

DELETE FROM test.produccion_cientifica;
ALTER SEQUENCE test.produccion_cientifica_seq RESTART WITH 1;

DELETE FROM test.configuracion_campo;
ALTER SEQUENCE test.configuracion_campo_seq RESTART WITH 1;

DELETE FROM test.modulador;
ALTER SEQUENCE test.modulador_seq RESTART WITH 1;

DELETE FROM test.rango;
ALTER SEQUENCE test.rango_seq RESTART WITH 1;

DELETE FROM test.puntuacion_grupo_investigador;
ALTER SEQUENCE test.puntuacion_grupo_investigador_seq RESTART WITH 1;

DELETE FROM test.puntuacion_grupo;
ALTER SEQUENCE test.puntuacion_grupo_seq RESTART WITH 1;

DELETE FROM test.baremo;
ALTER SEQUENCE test.baremo_seq RESTART WITH 1;

DELETE FROM test.convocatoria_baremacion_log;
ALTER SEQUENCE test.convocatoria_baremacion_log_seq RESTART WITH 1;

DELETE FROM test.convocatoria_baremacion;
ALTER SEQUENCE test.convocatoria_baremacion_seq RESTART WITH 1;

DELETE FROM test.tipo_fuente_impacto_cuartil;
ALTER SEQUENCE test.tipo_fuente_impacto_cuartil_seq RESTART WITH 1;

DELETE FROM test.configuracion_baremo;
ALTER SEQUENCE test.configuracion_baremo_seq RESTART WITH 1;

DELETE FROM test.alias_enumerado;
ALTER SEQUENCE test.alias_enumerado_seq RESTART WITH 1;

DELETE FROM test.indice_experimentalidad;
ALTER SEQUENCE test.indice_experimentalidad_seq RESTART WITH 1;

DELETE FROM test.tabla_indice;
ALTER SEQUENCE test.tabla_indice_seq RESTART WITH 1;

DELETE FROM test.editorial_prestigio;
ALTER SEQUENCE test.editorial_prestigio_seq RESTART WITH 1;

DELETE FROM test.tabla_editorial;
ALTER SEQUENCE test.tabla_editorial_seq RESTART WITH 1;

DELETE FROM test.mapeo_tipos;
ALTER SEQUENCE test.mapeo_tipos_seq RESTART WITH 1;
