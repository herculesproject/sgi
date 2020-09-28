export interface IConvocatoria {
  /** Id */
  id: number;

  /** Referencia */
  referencia: string;

  /** Titulo */
  titulo: string;

  /** Fecha Solicitud inicio */
  fechaInicioSolicitud: Date;

  /** Fecha Solicitud fin */
  fechaFinSolicitud: Date;

  /** Estado convocante */
  estadoConvocante: string;

  /** Plan Investigacion */
  planInvestigacion: string;

  /** Entidad Financiadora */
  entidadFinanciadora: string;

  /** Fuente Financiacion */
  fuenteFinanciacion: string;

  /** Activo */
  activo: boolean;

}
