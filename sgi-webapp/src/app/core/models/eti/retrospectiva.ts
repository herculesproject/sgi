
import { IPeticionEvaluacion } from './peticion-evaluacion';
import { Comite } from './comite';
import { TipoMemoria } from './tipo-memoria';
import { TipoEstadoMemoria } from './tipo-estado-memoria';

export interface IRetrospectiva {
  /** Id */
  id: number;

  /** Fecha retrospectiva. */
  fechaRetrospectiva: Date;


  /** Estado Retrospectiva. */
  estadoRetrospectiva: TipoEstadoMemoria;

}
