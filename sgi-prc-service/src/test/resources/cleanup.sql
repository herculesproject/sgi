-- vacia la bd
DELETE FROM test.autor_grupo;
DELETE FROM test.autor;
update test.produccion_cientifica set estado_produccion_cientifica_id = null;
DELETE FROM test.estado_produccion_cientifica;
DELETE FROM test.proyecto;
DELETE FROM test.acreditacion;
DELETE FROM test.indice_impacto;
DELETE FROM test.valor_campo;
DELETE FROM test.campo_produccion_cientifica;
DELETE FROM test.produccion_cientifica;
DELETE FROM test.configuracion_campo;
DELETE FROM test.alias_enumerado;


