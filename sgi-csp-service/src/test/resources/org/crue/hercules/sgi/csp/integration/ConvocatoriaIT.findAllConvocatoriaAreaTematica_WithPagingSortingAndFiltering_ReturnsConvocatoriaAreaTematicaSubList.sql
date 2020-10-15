
-- CONVOCATORIA
INSERT INTO csp.convocatoria (id, codigo, activo) VALUES (1, 'codigo-001', true);
INSERT INTO csp.convocatoria (id, codigo, activo) VALUES (2, 'codigo-002', true);

-- LISTADO AREA TEMATICA
INSERT INTO csp.listado_area_tematica (id, nombre, descripcion, activo) VALUES (1, 'nombre-001', 'descripcion-001', true);
INSERT INTO csp.listado_area_tematica (id, nombre, descripcion, activo) VALUES (2, 'nombre-002', 'descripcion-002', true);

-- AREA TEMATICA ARBOL
INSERT INTO csp.area_tematica_arbol (id, nombre, abreviatura, listado_area_tematica_id, area_tematica_arbol_padre_id, activo)  VALUES (1, 'nombre-001', 'A-1', 1L, null, true);
INSERT INTO csp.area_tematica_arbol (id, nombre, abreviatura, listado_area_tematica_id, area_tematica_arbol_padre_id, activo)  VALUES (2, 'nombre-002', 'A-2', 1L, 1L, true);
INSERT INTO csp.area_tematica_arbol (id, nombre, abreviatura, listado_area_tematica_id, area_tematica_arbol_padre_id, activo)  VALUES (3, 'nombre-003', 'A-3', 1L, null, true);
INSERT INTO csp.area_tematica_arbol (id, nombre, abreviatura, listado_area_tematica_id, area_tematica_arbol_padre_id, activo)  VALUES (4, 'nombre-004', 'A-4', 1L, 1L, true);
INSERT INTO csp.area_tematica_arbol (id, nombre, abreviatura, listado_area_tematica_id, area_tematica_arbol_padre_id, activo)  VALUES (5, 'nombre-005', 'A-5', 1L, null, true);
INSERT INTO csp.area_tematica_arbol (id, nombre, abreviatura, listado_area_tematica_id, area_tematica_arbol_padre_id, activo)  VALUES (6, 'nombre-006', 'A-6', 1L, 1L, true);

-- CONVOCATORIA AREA TEMATICA
INSERT INTO csp.convocatoria_area_tematica (id, convocatoria_id, area_tematica_id, observaciones) VALUES (2, 1, 1, 'observaciones-002');
INSERT INTO csp.convocatoria_area_tematica (id, convocatoria_id, area_tematica_id, observaciones) VALUES (1, 1, 2, 'observaciones-001');
INSERT INTO csp.convocatoria_area_tematica (id, convocatoria_id, area_tematica_id, observaciones) VALUES (3, 1, 3, 'observaciones-003');
INSERT INTO csp.convocatoria_area_tematica (id, convocatoria_id, area_tematica_id, observaciones) VALUES (4, 1, 4, 'observaciones-4');
INSERT INTO csp.convocatoria_area_tematica (id, convocatoria_id, area_tematica_id, observaciones) VALUES (5, 2, 5, 'observaciones-001');
INSERT INTO csp.convocatoria_area_tematica (id, convocatoria_id, area_tematica_id, observaciones) VALUES (6, 2, 6, 'observaciones-002');