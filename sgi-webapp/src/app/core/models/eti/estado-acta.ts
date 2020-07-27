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

  constructor() {
    this.id = null;
    this.tipoEstadoActa = null;
    this.fechaEstado = null;
    this.acta = null;
  }

}
