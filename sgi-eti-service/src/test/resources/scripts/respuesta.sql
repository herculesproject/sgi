-- DEPENDENCIAS: RESPUESTA
/*
  scripts = {  
  "classpath:scripts/memoria.sql",
  "classpath:scripts/apartado.sql"
  }
*/

-- RESPUESTA
INSERT INTO eti.respuesta (id, memoria_id, apartado_id, valor) VALUES (2, 2, 1, '{"valor":"Valor2"}');
INSERT INTO eti.respuesta (id, memoria_id, apartado_id, valor) VALUES (3, 2, 1, '{"valor":"Valor3"}');
INSERT INTO eti.respuesta (id, memoria_id, apartado_id, valor) VALUES (4, 2, 1, '{"valor":"Valor4"}');
INSERT INTO eti.respuesta (id, memoria_id, apartado_id, valor) VALUES (5, 2, 1, '{"valor":"Valor5"}');
INSERT INTO eti.respuesta (id, memoria_id, apartado_id, valor) VALUES (6, 2, 1, '{"valor":"Valor6"}');
INSERT INTO eti.respuesta (id, memoria_id, apartado_id, valor) VALUES (7, 2, 1, '{"valor":"Valor7"}');
INSERT INTO eti.respuesta (id, memoria_id, apartado_id, valor) VALUES (8, 2, 1, '{"valor":"Valor8"}');

ALTER SEQUENCE eti.respuesta_seq RESTART WITH 9;