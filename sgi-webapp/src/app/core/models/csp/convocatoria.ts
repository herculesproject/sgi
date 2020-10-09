import { IUnidadGestion } from './unidad-gestion';
import { IFinalidad } from './finalidad';
import { IAmbitoGeografico } from './ambito-geografico';
import { IRegimenConcurrencia } from './regimen-concurrencia';
import { IModeloEjecucion } from './tipos-configuracion';

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

  /** Estado. */
  estado: string;

  /** Unidad de gestión */
  unidadGestion: IUnidadGestion;

  /** Anio */
  anio: number;

  /** Modelo de ejecución */
  modeloEjecucion: IModeloEjecucion;

  /** Finalidad */
  finalidad: IFinalidad;

  /** Duración meses */
  duracionMeses: number;

  /** Ámbito geográfico */
  ambitoGeografico: IAmbitoGeografico;

  /** Clasificación Producción */
  clasificacionProduccion: string;

  /** Régimen concurrencia */
  regimenConcurrencia: IRegimenConcurrencia;

  /** Proyecto Colaborativo */
  proyectoColaborativo: string;

  /** Destinatarios */
  destinatarios: string;

  /** Entidad gestora */
  entidadGestora: string;

  /** Descripción convocatoria */
  descripcionConvocatoria: string;

  /** Observaciones */
  observaciones: string;

}
