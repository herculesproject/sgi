import { IConceptoGastoResponse } from '@core/services/csp/concepto-gasto/concepto-gasto-response';

export interface ISolicitudProyectoPresupuestoResponse {
  id: number;
  solicitudProyectoId: number;
  solicitudProyectoEntidadId: number;
  conceptoGasto: IConceptoGastoResponse;
  anualidad: number;
  importeSolicitado: number;
  importePresupuestado: number;
  observaciones: string;
}
