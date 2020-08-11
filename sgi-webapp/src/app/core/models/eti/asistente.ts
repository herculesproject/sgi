import { ConvocatoriaReunion } from './convocatoria-reunion';
import { Evaluador } from './evaluador';

export interface IAsistente {
  /** Id */
  id: number;

  /** Evaluador. */
  evaluador: Evaluador;

  /** Convocatoria de la reunión */
  convocatoriaReunion: ConvocatoriaReunion;

  /** Asistencia */
  asistencia: boolean;

  /** Motivo */
  motivo: string;

}
