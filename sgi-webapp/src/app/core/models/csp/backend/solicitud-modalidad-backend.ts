import { IPrograma } from '../programa';
import { ISolicitudBackend } from './solicitud-backend';

export interface ISolicitudModalidadBackend {
  /** Id */
  id: number;
  /** Solicitud */
  solicitud: ISolicitudBackend;
  /** EntidadRef */
  entidadRef: string;
  /** Programa */
  programa: IPrograma;
}
