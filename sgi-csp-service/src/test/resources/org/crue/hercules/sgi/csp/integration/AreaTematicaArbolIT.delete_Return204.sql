-- LISTADO AREA TEMATICA
INSERT INTO csp.listado_area_tematica (id, nombre, descripcion, activo) VALUES (1, 'nombre-001', 'descripcion-001', true);
INSERT INTO csp.listado_area_tematica (id, nombre, descripcion, activo) VALUES (2, 'nombre-002', 'descripcion-002', true);

-- AREA TEMATICA ARBOL
INSERT INTO csp.area_tematica_arbol (id, nombre, abreviatura, listado_area_tematica_id, area_tematica_arbol_padre_id, activo)  VALUES (1, 'nombre-001', 'A-1', 1L, null, true);
INSERT INTO csp.area_tematica_arbol (id, nombre, abreviatura, listado_area_tematica_id, area_tematica_arbol_padre_id, activo)  VALUES (2, 'nombre-002', 'A-2', 1L, 1L, true);
INSERT INTO csp.area_tematica_arbol (id, nombre, abreviatura, listado_area_tematica_id, area_tematica_arbol_padre_id, activo)  VALUES (3, 'nombre-003', 'A-3', 1L, 1L, true);
INSERT INTO csp.area_tematica_arbol (id, nombre, abreviatura, listado_area_tematica_id, area_tematica_arbol_padre_id, activo)  VALUES (4, 'nombre-004', 'A-4', 1L, 3L, true);
INSERT INTO csp.area_tematica_arbol (id, nombre, abreviatura, listado_area_tematica_id, area_tematica_arbol_padre_id, activo)  VALUES (11, 'nombre-011', 'A-11', 1L, null, true);
