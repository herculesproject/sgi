export interface IPeriodosJustificacion {
  /** Id */
  id: number;

  /** Num Periodo */
  numPeriodo: number;

  /** Mes inicial */
  mesInicial: Date;

  /** Mes final */
  mesFinal: Date;

  /** Fecha inicio */
  fechaInicio: Date;

  /** Fecha fin */
  fechaFin: Date;

  /** Observaciones */
  observaciones: string;

  /** Activo */
  activo: boolean;

}
