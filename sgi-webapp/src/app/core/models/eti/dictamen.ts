import { TipoEvaluacion } from './tipo-evaluacion';

export class Dictamen {
  /** Id */
  id: number;

  /** Nombre */
  nombre: string;

  /** Activo */
  activo: boolean;

  /** Tipo evaluacion */
  tipoEvaluacion: TipoEvaluacion;

}
