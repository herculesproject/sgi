import { TipoEstadoActa } from '../../../models/eti/tipo-estado-acta';
import { IConvocatoriaReunionBackend } from '../../../models/eti/backend/convocatoria-reunion-backend';

export interface IActaResponse {
  /** Id */
  id: number;
  /** Convocatoria de la reunión */
  convocatoriaReunion: IConvocatoriaReunionBackend;
  /** Hora inicio */
  horaInicio: number;
  /** Minuto inicio */
  minutoInicio: number;
  /** Hora fin */
  horaFin: number;
  /** Minuto fin */
  minutoFin: number;
  /** Resumen */
  resumen: string;
  /** Numero */
  numero: number;
  /** Inactiva */
  inactiva: boolean;
  /** Activo */
  activo: boolean;
  /** Estado Actual */
  estadoActual: TipoEstadoActa;
}
