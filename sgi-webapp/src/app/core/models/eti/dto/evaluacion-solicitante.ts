import { Persona } from '../../sgp/persona';
import { IEvaluacion } from '../evaluacion';

export interface IEvaluacionSolicitante extends IEvaluacion {
  persona: Persona;
}
