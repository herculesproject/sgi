import { IConvocatoriaReunionBackend } from '../../../models/eti/backend/convocatoria-reunion-backend';
import { IEvaluadorBackend } from '../../../models/eti/backend/evaluador-backend';
import { IComite } from '../../../models/eti/comite';
import { IDictamen } from '../../../models/eti/dictamen';
import { TipoEvaluacion } from '../../../models/eti/tipo-evaluacion';
import { IMemoriaResponse } from '../memoria/memoria-response';

export interface IEvaluacionResponse {
  /** ID */
  id: number;
  /** Memoria */
  memoria: IMemoriaResponse;
  /** Comite */
  comite: IComite;
  /** Convocatoria reunión */
  convocatoriaReunion: IConvocatoriaReunionBackend;
  /** Tipo evaluación */
  tipoEvaluacion: TipoEvaluacion;
  /** Version */
  version: number;
  /** Dictamen */
  dictamen: IDictamen;
  /** Evaluador 1 */
  evaluador1: IEvaluadorBackend;
  /** Evaluador 2 */
  evaluador2: IEvaluadorBackend;
  /** Fecha Inicio. */
  fechaDictamen: string;
  /** Es revisión mínima */
  esRevMinima: boolean;
  /** Comentario */
  comentario: string;
  /** Activo */
  activo: boolean;
}
