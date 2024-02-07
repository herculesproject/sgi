-- DEPENDENCIAS: FORMLY
/*
  scripts = { 
    "classpath:scripts/formly.sql",
  }
*/
-- FORMPLY
INSERT INTO test.formly (id, version) VALUES (1, 1);
INSERT INTO test.formly (id, version) VALUES (2, 1);
INSERT INTO test.formly (id, version) VALUES (3, 2);
INSERT INTO test.formly (id, version) VALUES (4, 3);
INSERT INTO test.formly (id, version) VALUES (5, 2);

INSERT INTO test.formly_nombre (formly_id, nombre, esquema, lang) VALUES (1, 'FRM001', '{}', 'en');
INSERT INTO test.formly_nombre (formly_id, nombre, esquema, lang) VALUES (2, 'FRM002', '{}', 'en');
INSERT INTO test.formly_nombre (formly_id, nombre, esquema, lang) VALUES (3, 'FRM003', '{}', 'en');
INSERT INTO test.formly_nombre (formly_id, nombre, esquema, lang) VALUES (4, 'FRM004', '{}', 'en');
INSERT INTO test.formly_nombre (formly_id, nombre, esquema, lang) VALUES (5, 'FRM005', '{}', 'en');

INSERT INTO test.formly (id, version) VALUES (6, 1);
INSERT INTO test.formly (id, version) VALUES (7, 2);

INSERT INTO test.formly_nombre (formly_id, nombre, esquema, lang) VALUES (6, 'FRM006', '{"name":"CHECKLIST","version":1}', 'en');
INSERT INTO test.formly_nombre (formly_id, nombre, esquema, lang) VALUES (6, 'EU:FRM006', '{"name":"CHECKLIST","version":1}', 'es');
INSERT INTO test.formly_nombre (formly_id, nombre, esquema, lang) VALUES (7, 'FRM007', '{"name":"CHECKLIST","version":1}', 'en');
INSERT INTO test.formly_nombre (formly_id, nombre, esquema, lang) VALUES (7, 'EU:FRM007', '{"name":"CHECKLIST","version":1}', 'es');

ALTER SEQUENCE test.formly_seq RESTART WITH 8;