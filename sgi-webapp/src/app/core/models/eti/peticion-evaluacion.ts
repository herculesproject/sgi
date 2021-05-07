import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DateTime } from 'luxon';
import { IPersona } from '../sgp/persona';
import { ITipoActividad } from './tipo-actividad';
import { ITipoInvestigacionTutelada } from './tipo-investigacion-tutelada';

export interface IPeticionEvaluacion {
  /** ID */
  id: number;
  /** Solicitud convocatoria ref */
  solicitudConvocatoriaRef: string;
  /** Código */
  codigo: string;
  /** Título */
  titulo: string;
  /** Tipo de actividad */
  tipoActividad: ITipoActividad;
  /** Tipo de investigacion tutelada */
  tipoInvestigacionTutelada: ITipoInvestigacionTutelada;
  /** Existe fuente financiacion */
  existeFinanciacion: boolean;
  /** Referencia fuente financiacion */
  fuenteFinanciacion: string;
  /** Estado fuente financiacion */
  estadoFinanciacion: EstadoFinanciacion;
  /** Importe fuente financiacion */
  importeFinanciacion: boolean;
  /** Fecha Inicio. */
  fechaInicio: DateTime;
  /** Fecha Fin. */
  fechaFin: DateTime;
  /** Resumen */
  resumen: string;
  /** Valor social */
  valorSocial: number;
  /** Objetivos */
  objetivos: string;
  /** Diseño metodológico */
  disMetodologico: string;
  /** Externo */
  externo: boolean;
  /** Tiene fondos propios */
  tieneFondosPropios: boolean;
  /** Referencia persona */
  solicitante: IPersona;
  /** Activo */
  activo: boolean;
}

export enum EstadoFinanciacion {
  SOLICITADO = 'SOLICITADO',
  CONCEDIDO = 'CONCEDIDO',
  DENEGADO = 'DENEGADO'
}

export const ESTADO_FINANCIACION_MAP: Map<EstadoFinanciacion, string> = new Map([
  [EstadoFinanciacion.SOLICITADO, marker(`eti.peticion-evaluacion.estado-financiacion.SOLICITADO`)],
  [EstadoFinanciacion.CONCEDIDO, marker(`eti.peticion-evaluacion.estado-financiacion.CONCEDIDO`)],
  [EstadoFinanciacion.DENEGADO, marker(`eti.peticion-evaluacion.estado-financiacion.DENEGADO`)]
]);
