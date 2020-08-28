import { Servicio } from './servicio';

export class Registro {

  /** ID */
  id: number;
  /** personaRef */
  personaRef: string;
  /** servicio */
  servicio: Servicio;
  /** Acepta normativa */
  aceptaCondiciones: boolean;

  constructor() {
    this.id = null;
    this.servicio = null;
    this.aceptaCondiciones = false;
    this.personaRef = 'user-1090';
  }

}
