import { ITipoPeriodoJustificacion } from './tipo-periodo-justificacion';

export interface IPeriodoJustificacion {
  /** Id */
  id: number;

  /** Num Periodo */
  numPeriodo: number;

  /** Tipo */
  tipoJustificacion: ITipoPeriodoJustificacion;

  /** Mes inicial */
  mesInicial: string;

  /** Mes final */
  mesFinal: string;

  /** Fecha inicio */
  fechaInicio: Date;

  /** Fecha fin */
  fechaFin: Date;

  /** Observaciones */
  observaciones: string;

  /** Activo */
  activo: boolean;

}
