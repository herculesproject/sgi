import { Memoria } from './memoria';
import { TipoEstadoMemoria } from './tipo-estado-memoria';

export class EstadoMemoria {

  /** ID */
  id: number;
  /** Memoria */
  memoria: Memoria;
  /** Tipo estado memoria */
  tipoEstadoMemoria: TipoEstadoMemoria;
  /** Fecha Estado */
  fechaEstado: Date;

  constructor(memoria: Memoria, tipoEstadoMemoria: TipoEstadoMemoria, fechaEstado: Date) {
    this.tipoEstadoMemoria = tipoEstadoMemoria;
    this.fechaEstado = fechaEstado;
    this.memoria = memoria;
  }

}
