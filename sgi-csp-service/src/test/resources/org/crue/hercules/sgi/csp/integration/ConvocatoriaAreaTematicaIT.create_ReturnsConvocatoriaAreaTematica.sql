-- CONVOCATORIA
INSERT INTO csp.convocatoria (id, codigo, activo) VALUES (1, 'codigo-001', true);

-- LISTADO AREA TEMATICA
INSERT INTO csp.listado_area_tematica (id, nombre, descripcion, activo) VALUES (1, 'nombre-001', 'descripcion-001', true);

-- AREA TEMATICA ARBOL
INSERT INTO csp.area_tematica_arbol (id, nombre, abreviatura, listado_area_tematica_id, area_tematica_arbol_padre_id, activo)  VALUES (1, 'nombre-001', 'A-1', 1L, null, true);
INSERT INTO csp.area_tematica_arbol (id, nombre, abreviatura, listado_area_tematica_id, area_tematica_arbol_padre_id, activo)  VALUES (2, 'nombre-002', 'A-2', 1L, 1L, true);
INSERT INTO csp.area_tematica_arbol (id, nombre, abreviatura, listado_area_tematica_id, area_tematica_arbol_padre_id, activo)  VALUES (3, 'nombre-003', 'A-3', 1L, null, true);
