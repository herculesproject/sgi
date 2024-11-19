import { IConceptoGastoResponse } from '@core/services/csp/concepto-gasto/concepto-gasto-response';

export interface IConvocatoriaConceptoGastoResponse {
  /** id */
  id: number;
  /** ConceptoGasto */
  conceptoGasto: IConceptoGastoResponse;
  /** Id de Convocatoria */
  convocatoriaId: number;
  /** Observaciones */
  observaciones: string;
  /** Importe máximo */
  importeMaximo: number;
  /** Porcentaje máximo */
  porcentajeMaximo: number;
  /** Permitido */
  permitido: boolean;
  /** Mes inicial */
  mesInicial: number;
  /** Mes final */
  mesFinal: number;
}
