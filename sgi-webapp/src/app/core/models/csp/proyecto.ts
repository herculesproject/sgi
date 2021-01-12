import { ClasificacionCVN } from '@core/enums/clasificacion-cvn';
import { IUnidadGestion } from '../usr/unidad-gestion';
import { IConvocatoria } from './convocatoria';
import { IEstadoProyecto } from './estado-proyecto';
import { ISolicitud } from './solicitud';
import { ITipoAmbitoGeografico } from './tipo-ambito-geografico';
import { IModeloEjecucion, ITipoFinalidad } from './tipos-configuracion';

export enum TipoPlantillaJustificacionEnum {
  N = 'N/A'
}

export enum TipoHojaFirmaEnum {
  N = 'N/A'
}

export enum TipoHorasAnualesEnum {
  VALOR_FIJO = 'Valor fijo',
  REALES = 'Reales (TS)',
  POR_CATEGORIA = 'Por categoría'
}

export interface IProyecto {

  /** Id */
  id: number;

  /** EstadoProyecto */
  estado: IEstadoProyecto;

  /** Titulo */
  titulo: string;

  /** Acronimo */
  acronimo: string;

  /** codigoExterno */
  codigoExterno: string;

  /** Fecha Inicio */
  fechaInicio: Date;

  /** Fecha Fin */
  fechaFin: Date;

  /** Comentario */
  comentario: string;

  /** Unidad gestion */
  unidadGestion: IUnidadGestion;

  /** modelo ejecucion */
  modeloEjecucion: IModeloEjecucion;

  /** convocatoriaExterna */
  convocatoriaExterna: string;

  /** finalidad */
  finalidad: ITipoFinalidad;

  /** convocatoria */
  convocatoria: IConvocatoria;

  /** solicitud */
  solicitud: ISolicitud;

  /** ambitoGeografico */
  ambitoGeografico: ITipoAmbitoGeografico;

  /** plantillaJustificacion */
  plantillaJustificacion: TipoPlantillaJustificacionEnum;

  /** confidencial */
  confidencial: boolean;

  /** clasificacionCVN */
  clasificacionCVN: ClasificacionCVN;

  /** colaborativo */
  colaborativo: boolean;

  /** coordinadorExterno */
  coordinadorExterno: boolean;

  /** uniSubcontratada */
  uniSubcontratada: boolean;

  /** timesheet */
  timesheet: boolean;

  /** plantillaHojaFirma */
  plantillaHojaFirma: TipoHojaFirmaEnum;

  /** paquetesTrabajo */
  paquetesTrabajo: boolean;

  /** costeHora */
  costeHora: boolean;

  /** tipoHorasAnuales */
  tipoHorasAnuales: TipoHorasAnualesEnum;

  /** contratos */
  contratos: boolean;

  /** facturacion */
  facturacion: boolean;

  /** iva */
  iva: boolean;

  /** observaciones */
  observaciones: string;

  /** finalista */
  finalista: boolean;

  /** limitativo */
  limitativo: boolean;

  /** anualidades */
  anualidades: boolean;

  /** activo  */
  activo: boolean;
}
