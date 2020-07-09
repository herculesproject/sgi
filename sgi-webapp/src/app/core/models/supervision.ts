import { Servicio } from './servicio';

export class Supervision {

  /** ID */
  id: number;

  /** Usuario ref. */
  usuarioRef: string;

  /** Servicio. */
  servicio: Servicio;

  constructor(usuarioRef: string, servicio: Servicio) {
    this.usuarioRef = usuarioRef;
    this.servicio = servicio;
  }

}
