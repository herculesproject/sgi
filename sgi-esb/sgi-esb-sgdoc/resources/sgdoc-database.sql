CREATE SCHEMA sgdoc;

CREATE TABLE sgdoc.documento (
  id VARCHAR(50) NOT NULL,
  nombre VARCHAR(250),
  version VARCHAR(50),
  archivo VARCHAR(250),
  fecha_creacion TIMESTAMP,
  tipo VARCHAR(250),
  autor_ref VARCHAR(50),
  CONSTRAINT documento_pk PRIMARY KEY (id)
);
