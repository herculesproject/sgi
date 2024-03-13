import { marker } from "@biesbjerg/ngx-translate-extract-marker";

export enum ValidacionClasificacionGastos {
  VALIDACION = 'VALIDACION',
  CLASIFICACION = 'CLASIFICACION',
  ELEGIBILIDAD = 'ELEGIBILIDAD'
}

export enum CardinalidadRelacionSgiSge {
  SGI_1_SGE_1 = 'SGI_1_SGE_1',
  SGI_1_SGE_N = 'SGI_1_SGE_N',
  SGI_N_SGE_1 = 'SGI_N_SGE_1',
  SGI_N_SGE_N = 'SGI_N_SGE_N'
}

export const CARDINALIDAD_RELACION_SGI_SGE_MAP: Map<CardinalidadRelacionSgiSge, string> = new Map([
  [CardinalidadRelacionSgiSge.SGI_1_SGE_1, marker(`csp.cardinalidad-relacion-sgi-sge.SGI_1_SGE_1`)],
  [CardinalidadRelacionSgiSge.SGI_1_SGE_N, marker(`csp.cardinalidad-relacion-sgi-sge.SGI_1_SGE_N`)],
  [CardinalidadRelacionSgiSge.SGI_N_SGE_1, marker(`csp.cardinalidad-relacion-sgi-sge.SGI_N_SGE_1`)],
  [CardinalidadRelacionSgiSge.SGI_N_SGE_N, marker(`csp.cardinalidad-relacion-sgi-sge.SGI_N_SGE_N`)]
]);

export const VALIDACION_CLASIFICACION_GASTOS_MAP: Map<ValidacionClasificacionGastos, string> = new Map([
  [ValidacionClasificacionGastos.VALIDACION, marker(`csp.validacion-clasificacion-gastos.VALIDACION`)],
  [ValidacionClasificacionGastos.CLASIFICACION, marker(`csp.validacion-clasificacion-gastos.CLASIFICACION`)],
  [ValidacionClasificacionGastos.ELEGIBILIDAD, marker(`csp.validacion-clasificacion-gastos.ELEGIBILIDAD`)]
]);

export interface IConfiguracion {
  id: number;
  /** Expresión regular para validar el formato del código de las partidas presupuestarias */
  formatoPartidaPresupuestaria: string;
  /** Plantilla informativa del formato del código de las partidas presupuestarias */
  plantillaFormatoPartidaPresupuestaria: string;
  /** Determina cuándo la validación de gastos está activa en la app */
  validacionClasificacionGastos: ValidacionClasificacionGastos;
  /** Expresión regular para validar el formato del código de los identificadores de justificación */
  formatoIdentificadorJustificacion: string;
  /** Plantilla informativa del formato del código de los identificadores de justificación */
  plantillaFormatoIdentificadorJustificacion: string;
  /** Dedicacion minima de un miembro de un grupo de investigacion */
  dedicacionMinimaGrupo: number;
  /** Expresión regular para validar el formato del código interno de proyecto */
  formatoCodigoInternoProyecto: string;
  /** Plantilla informativa del formato del código interno de proyecto */
  plantillaFormatoCodigoInternoProyecto: string;
  /** Habilitar Ejecución económica de Grupos de investigación */
  ejecucionEconomicaGruposEnabled: boolean;
  /** Cardinalidad relación proyecto SGI - identificador SGE */
  cardinalidadRelacionSgiSge: CardinalidadRelacionSgiSge;
  /** Habilitar creación de Partidas presupuestarias en el SGE */
  partidasPresupuestariasSgeEnabled: boolean;
  /** Habilitar creación de Periodos de amortización en el SGE */
  amortizacionFondosSgeEnabled: boolean;
  /** Habilitar la integración de gastos justificados (apartado seguimiento de justificación) SGE */
  gastosJustificadosSgeEnabled: boolean;
  /** Habilitar la acción de solicitar modificación de los datos del proyecto SGE */
  modificacionProyectoSgeEnabled: boolean;
  /** Habilitar la visualización del campo Sector IVA proveniente de la integración con el SGE */
  sectorIvaSgeEnabled: boolean;
}
