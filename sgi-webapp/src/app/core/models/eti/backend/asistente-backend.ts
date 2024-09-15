import { IConvocatoriaReunionResponse } from '../../../services/eti/convocatoria-reunion/convocatoria-reunion-response';
import { IEvaluadorResponse } from '../../../services/eti/evaluador/evaluador-response';

export interface IAsistenteBackend {
  /** Id */
  id: number;
  /** Evaluador. */
  evaluador: IEvaluadorResponse;
  /** Convocatoria de la reunión */
  convocatoriaReunion: IConvocatoriaReunionResponse;
  /** Asistencia */
  asistencia: boolean;
  /** Motivo */
  motivo: string;
}
