import { Servicio } from './servicio';

export class Registro {

  /** ID */
  id: number;
  /** usuarioRef */
  usuarioRef: string;
  /** servicio */
  servicio: Servicio;
  /** Acepta normativa */
  aceptaCondiciones: boolean;

  constructor() {
    this.id = null;
    this.servicio = null;
    this.aceptaCondiciones = false;
    this.usuarioRef = 'user-1090';
  }

}
