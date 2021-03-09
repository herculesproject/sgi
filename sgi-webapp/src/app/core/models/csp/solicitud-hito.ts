import { DateTime } from 'luxon';
import { ISolicitud } from './solicitud';
import { ITipoHito } from './tipos-configuracion';

export interface ISolicitudHito {
  /** Id */
  id: number;
  /** Solicitud */
  solicitud: ISolicitud;
  /** fecha */
  fecha: DateTime;
  /** Comentario */
  comentario: string;
  /** Comentario */
  generaAviso: boolean;
  /** Tipo Hito */
  tipoHito: ITipoHito;
}
