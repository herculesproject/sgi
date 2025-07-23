import { I18nFieldValueResponse } from '@core/i18n/i18n-field-response';
import { IEvaluadorBackend } from '../../../models/eti/backend/evaluador-backend';
import { IDictamen } from '../../../models/eti/dictamen';
import { TipoEvaluacion } from '../../../models/eti/tipo-evaluacion';
import { IComiteResponse } from '../comite/comite-response';
import { IConvocatoriaReunionResponse } from '../convocatoria-reunion/convocatoria-reunion-response';
import { IMemoriaResponse } from '../memoria/memoria-response';

export interface IEvaluacionResponse {
  /** ID */
  id: number;
  /** Memoria */
  memoria: IMemoriaResponse;
  /** Comite */
  comite: IComiteResponse;
  /** Convocatoria reunión */
  convocatoriaReunion: IConvocatoriaReunionResponse;
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
  comentario: I18nFieldValueResponse[];
  /** Activo */
  activo: boolean;
}
