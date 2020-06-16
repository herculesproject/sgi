import { Servicio } from './servicio';

export class TipoFungible {

  /** ID */
  id: number;
  /** nombre */
  nombre: string;
  /** servicio */
  servicio: Servicio;

  constructor() {
    this.id = null;
    this.nombre = '';
    this.servicio = null;
  }

}
