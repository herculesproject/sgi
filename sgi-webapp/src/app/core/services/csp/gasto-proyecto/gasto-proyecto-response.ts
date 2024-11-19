import { Estado } from '@core/models/csp/estado-gasto-proyecto';
import { IConceptoGastoResponse } from '../concepto-gasto/concepto-gasto-response';

export interface IGastoProyectoResponse {
  id: number;
  proyectoId: number;
  gastoRef: string;
  conceptoGasto: IConceptoGastoResponse;
  estado: {
    id: number;
    estado: Estado;
    fechaEstado: string;
    comentario: string;
  };
  fechaCongreso: string;
  importeInscripcion: number;
  observaciones: string;
}
