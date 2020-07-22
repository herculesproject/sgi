import { ConvocatoriaReunion } from './convocatoria-reunion';
import { EstadoActa } from './estado-acta';

export class Acta {
  /** Id */
  id: number;

  /** Convocatoria de la reunión */
  convocatoriaReunion: ConvocatoriaReunion;

  /** Hora inicio */
  horaInicio: number;

  /** Minuto inicio */
  minutoInicio: number;

  /** Hora fin */
  horaFin: number;

  /** Minuto fin */
  minutoFin: number;

  /** Resumen */
  resumen: string;

  /** Numero */
  numero: number;

  /** Inactiva */
  inactiva: boolean;

  /** Activo */
  activo: boolean;

  /** Estado Acta */
  estadoActa: EstadoActa;

  constructor() {
    this.id = null;
    this.convocatoriaReunion = new ConvocatoriaReunion();
    this.horaInicio = 0;
    this.minutoInicio = 0;
    this.horaFin = 0;
    this.minutoFin = 0;
    this.resumen = '';
    this.numero = 0;
    this.inactiva = false;
    this.activo = true;
    this.estadoActa = new EstadoActa();
  }
}
