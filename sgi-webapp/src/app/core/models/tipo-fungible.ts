import { Servicio } from './servicio';

export class TipoFungible {

  /** ID */
  id: number;
  /** nombre */
  nombre: string;
  /** servicio */
  servicio: Servicio;
  /** activo */
  activo: boolean;

  constructor() {
    this.id = null;
    this.nombre = '';
    this.servicio = null;
    this.activo = null;
  }

}
