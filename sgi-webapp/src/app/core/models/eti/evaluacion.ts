import { Comite } from './comite';
import { ConvocatoriaReunion } from './convocatoria-reunion';
import { Dictamen } from './dictamen';
import { Memoria } from './memoria';
import { TipoEvaluacion } from './tipo-evaluacion';
import { IEvaluador } from './evaluador';

export interface IEvaluacion {
  /** ID */
  id: number;

  /** Memoria */
  memoria: Memoria;

  /** Comite */
  comite: Comite;

  /** Convocatoria reunión */
  convocatoriaReunion: ConvocatoriaReunion;

  /** Tipo evaluación */
  tipoEvaluacion: TipoEvaluacion;

  /** Version */
  version: number;

  /** Dictamen */
  dictamen: Dictamen;

  /** Evaluador 1 */
  evaluador1: IEvaluador;

  /** Evaluador 2 */
  evaluador2: IEvaluador;

  /** Fecha Inicio. */
  fechaDictamen: Date;

  /** Es revisión mínima */
  esRevMinima: boolean;

  /** Activo */
  activo: boolean;
}
