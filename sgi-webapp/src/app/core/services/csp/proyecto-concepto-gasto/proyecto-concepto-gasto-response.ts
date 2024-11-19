import { IConceptoGastoResponse } from "../concepto-gasto/concepto-gasto-response";

export interface IProyectoConceptoGastoResponse {
  id: number;
  proyectoId: number;
  conceptoGasto: IConceptoGastoResponse;
  permitido: boolean;
  importeMaximo: number;
  fechaInicio: string;
  fechaFin: string;
  observaciones: string;
  convocatoriaConceptoGastoId: number;
}
