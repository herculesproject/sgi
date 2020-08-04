import { DatosEmail } from './datos-email';
import { DatosTelefono } from './datos-telefono';
import { Profesional } from './profesional';
import { Usuario } from './usuario';

export interface UsuarioInfo extends Usuario {

  /** Tipo Identificador */
  tipoIdentificador: string;

  /** Número identificador personal */
  numIdentificadorPersonal: string;

  /** Domicilio */
  domicilio: string;

  /** Fecha nacimiento */
  fechaNacimiento: string;

  /** Nacionalidad */
  nacionalidad: string;

  /** Sexo */
  sexo: string;

  /** Datos email */
  datosEmail: DatosEmail;

  /** Datos teléfono */
  datosTelefono: DatosTelefono;

  /** Vinculación */
  vinculacion: string;

  /** Profesional */
  profesional: Profesional;

}
