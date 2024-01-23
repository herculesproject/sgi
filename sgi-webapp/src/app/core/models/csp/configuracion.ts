import { marker } from "@biesbjerg/ngx-translate-extract-marker";

export enum ValidacionClasificacionGastos {
  VALIDACION = 'VALIDACION',
  CLASIFICACION = 'CLASIFICACION',
  ELEGIBILIDAD = 'ELEGIBILIDAD'
}

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
  /** Habilitar Ejecución económica de Grupos de investigació */
  ejecucionEconomicaGruposEnabled: boolean;
}
