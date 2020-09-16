import { PeticionEvaluacion } from './peticion-evaluacion';
import { Persona } from '../sgp/persona';

export interface IEquipoTrabajo extends Persona {

  /** ID */
  id: number;

  /** Peticion evaluación */
  peticionEvaluacion: PeticionEvaluacion;

  /** Flag eliminable */
  isEliminable: boolean;

}
