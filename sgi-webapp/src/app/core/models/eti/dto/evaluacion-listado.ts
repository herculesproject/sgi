import { Persona } from '@core/models/sgp/persona';

import { IEvaluacion } from '../evaluacion';

export interface EvaluacionListado extends IEvaluacion {
  persona: Persona;
}
