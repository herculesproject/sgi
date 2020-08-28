import { ConvocatoriaReunion } from './convocatoria-reunion';
import { IEvaluador } from './evaluador';

export interface IAsistente {
  /** Id */
  id: number;

  /** Evaluador. */
  evaluador: IEvaluador;

  /** Convocatoria de la reunión */
  convocatoriaReunion: ConvocatoriaReunion;

  /** Asistencia */
  asistencia: boolean;

  /** Motivo */
  motivo: string;

}
