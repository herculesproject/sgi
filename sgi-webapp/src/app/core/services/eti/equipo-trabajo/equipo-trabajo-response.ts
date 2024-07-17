import { IPeticionEvaluacionResponse } from '../peticion-evaluacion/peticion-evaluacion-response';

export interface IEquipoTrabajoResponse {
  /** ID */
  id: number;
  /** Persona ref */
  personaRef: string;
  /** Peticion evaluación */
  peticionEvaluacion: IPeticionEvaluacionResponse;
}
