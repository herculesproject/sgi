import { Seccion } from './seccion';

export class Servicio {

  /** ID */
  id: number;
  /** nombre */
  nombre: string;
  /** abreviatura */
  abreviatura: string;
  /** contacto */
  contacto: string;
  /** seccion */
  seccion: Seccion;
  /** activo */
  activo: boolean;

  constructor() {
    this.id = null;
    this.nombre = '';
    this.abreviatura = '';
    this.contacto = '';
    this.seccion = null;
    this.activo = true;
  }

}
