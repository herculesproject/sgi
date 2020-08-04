import { Acta } from './acta';
import { TipoEstadoActa } from './tipo-estado-acta';

export class EstadoActa {

  /** ID */
  id: number;
  /** Acta */
  acta: Acta;
  /** Tipo estado acta */
  tipoEstadoActa: TipoEstadoActa;
  /** Fecha Estado */
  fechaEstado: Date;

  constructor(acta: Acta, tipoEstadoActa: TipoEstadoActa, fechaEstado: Date) {
    this.tipoEstadoActa = tipoEstadoActa;
    this.fechaEstado = fechaEstado;
    this.acta = acta;
  }

}
