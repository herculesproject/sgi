import { TipoEstadoActa } from '@core/enums/eti/tipo-estado-acta';

export class EstadoActa {
  /** Id */
  id: number;

  /** Tipo Estado Acta  */
  tipoEstadoActa: TipoEstadoActa;

  /** Fecha Estado */
  fechaEstado: Date;

  constructor() {
    this.id = null;
    this.tipoEstadoActa = TipoEstadoActa.EN_ELABORACION;
    this.fechaEstado = null;
  }
}
