import { Servicio } from './servicio';

export class Supervision {

  /** ID */
  id: number;

  /** Persona ref. */
  personaRef: string;

  /** Servicio. */
  servicio: Servicio;

  constructor(personaRef: string, servicio: Servicio) {
    this.personaRef = personaRef;
    this.servicio = servicio;
  }

}
