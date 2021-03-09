import { ITipoHito } from '../tipos-configuracion';
import { ISolicitudBackend } from './solicitud-backend';

export interface ISolicitudHitoBackend {
  /** Id */
  id: number;
  /** Solicitud */
  solicitud: ISolicitudBackend;
  /** fecha */
  fecha: string;
  /** Comentario */
  comentario: string;
  /** Comentario */
  generaAviso: boolean;
  /** Tipo Hito */
  tipoHito: ITipoHito;
}
