import { IConvocatoriaReunionResponse } from '../../../services/eti/convocatoria-reunion/convocatoria-reunion-response';
import { IEvaluadorBackend } from './evaluador-backend';

export interface IAsistenteBackend {
  /** Id */
  id: number;
  /** Evaluador. */
  evaluador: IEvaluadorBackend;
  /** Convocatoria de la reunión */
  convocatoriaReunion: IConvocatoriaReunionResponse;
  /** Asistencia */
  asistencia: boolean;
  /** Motivo */
  motivo: string;
}
