CREATE SCHEMA sgp;


CREATE TABLE sgp.profesional (
  id VARCHAR(50) NOT NULL,
  categoria_profesional VARCHAR(250),
  cuerpo_profesional VARCHAR(250),
  subcuerpo_profesional VARCHAR(250),
  CONSTRAINT profesional_pk PRIMARY KEY (id)
);

CREATE TABLE sgp.persona (
  id VARCHAR(50) NOT NULL,
  nombre VARCHAR(50),
  primer_apellido VARCHAR(50),
  segundo_apellido VARCHAR(50),
  identificador_numero VARCHAR(50),
  identificador_letra VARCHAR(1),
  CONSTRAINT persona_pk PRIMARY KEY (id)
);

CREATE TABLE sgp.persona_fisica (
  id VARCHAR(50) NOT NULL,
  fecha_nacimiento VARCHAR(50),
  nacionalidad VARCHAR(50),
  sexo VARCHAR(50),
  tipo_persona VARCHAR(50),
  vinculacion VARCHAR(50),
  profesional_id VARCHAR(50),
  CONSTRAINT persona_fisica_pk PRIMARY KEY (id),
  CONSTRAINT persona_fisica_profesional_fk FOREIGN KEY (profesional_id) REFERENCES profesional(id)
);

CREATE TABLE sgp.domicilio (
  id VARCHAR(50) NOT NULL,
  persona_id VARCHAR(50),
  tipo_via VARCHAR(50),
  nombre_via VARCHAR(250),
  numero VARCHAR(50),
  poblacion VARCHAR(250),
  codigo_postal VARCHAR(50),
  provincia VARCHAR(50),
  pais VARCHAR(50),
  ampliacion_domicilio VARCHAR(250),
  CONSTRAINT domicilio_pk PRIMARY KEY (id)
);

CREATE TABLE sgp.email (
  id VARCHAR(50) NOT NULL,
  persona_id VARCHAR(50),
  email VARCHAR(250),
  tipo_email VARCHAR(50),
  CONSTRAINT email_pk PRIMARY KEY (id)
);

CREATE TABLE sgp.telefono (
  id VARCHAR(50) NOT NULL,
  persona_id VARCHAR(50),
  telefono VARCHAR(50),
  tipo_telefono VARCHAR(50),
  CONSTRAINT telefono_pk PRIMARY KEY (id)
);

CREATE TABLE sgp.persona_juridica (
  id VARCHAR(50) NOT NULL,
  razon_social VARCHAR(250),
  identificador_numero VARCHAR(50),
  identificador_letra VARCHAR(50),
  tipo_persona VARCHAR(50),
  persona_contacto_id VARCHAR(50),
  CONSTRAINT persona_juridica_pk PRIMARY KEY (id),
  CONSTRAINT persona_juridica_persona_contacto_fk FOREIGN KEY (persona_contacto_id) REFERENCES persona(id)
);

CREATE TABLE sgp.empresa_economica (
  id VARCHAR(50) NOT NULL,
  tipo_documento VARCHAR(50),
  numero_documento VARCHAR(50),
  empresa_economica_padre_id VARCHAR(50),
  razon_social VARCHAR(250),
  direccion VARCHAR(250),
  tipo_empresa VARCHAR(250),
  CONSTRAINT empresa_economica_pk PRIMARY KEY (id)
);

ALTER TABLE sgp.empresa_economica ADD CONSTRAINT empresa_economica_padre_fk FOREIGN KEY (empresa_economica_padre_id) REFERENCES sgp.empresa_economica(id);




