CREATE SCHEMA sge;

CREATE TABLE sge.codigo_economico (
  id VARCHAR(50) NOT NULL,
  codigo VARCHAR(250),
  fecha_inicio VARCHAR(50),
  fecha_fin VARCHAR(50),
  tipo VARCHAR(50),
  CONSTRAINT codigo_economico_pk PRIMARY KEY (id)
);




