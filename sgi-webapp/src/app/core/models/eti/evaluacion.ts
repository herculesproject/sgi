import { Memoria } from './memoria';
import { ConvocatoriaReunion } from './convocatoria-reunion';
import { TipoEvaluacion } from './tipo-evaluacion';
import { Dictamen } from './dictamen';

export class Evaluacion {

  /** ID */
  id: number;

  /** Memoria */
  memoria: Memoria;

  /** Convocatoria reunión */
  convocatoriaReunion: ConvocatoriaReunion;

  /** Tipo evaluación */
  tipoEvaluacion: TipoEvaluacion;

  /** Version */
  version: number;

  /** Dictamen */
  dictamen: Dictamen;

  /** Fecha Inicio. */
  fechaDictamen: Date;

  /** Es revisión mínima */
  esRevMinima: boolean;

  /** Activo */
  activo: boolean;

}
