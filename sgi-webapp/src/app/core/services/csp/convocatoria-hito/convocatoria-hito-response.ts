import { ITipoHito } from '@core/models/csp/tipos-configuracion';
import { ITipoHitoResponse } from '../tipo-hito/tipo-hito-response';

export interface IConvocatoriaHitoResponse {
  /** Id */
  id: number;
  /** Fecha inicio  */
  fecha: string;
  /** Tipo de hito */
  tipoHito: ITipoHitoResponse;
  /** Comentario */
  comentario: string;
  /** Id de Convocatoria */
  convocatoriaId: number;
  aviso: {
    comunicadoRef: string;
    tareaProgramadaRef: string;
    incluirIpsSolicitud: boolean;
    incluirIpsProyecto: boolean;
  };
}
