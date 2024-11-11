import { ITipoHitoResponse } from '../tipo-hito/tipo-hito-response';

export interface IProyectoHitoResponse {
  /** Id */
  id: number;
  /** Fecha inicio  */
  fecha: string;
  /** Tipo de hito */
  tipoHito: ITipoHitoResponse;
  /** Comentario */
  comentario: string;
  /** Id de Proyecto */
  proyectoId: number;
  proyectoHitoAviso: {
    comunicadoRef: string;
    tareaProgramadaRef: string;
    incluirIpsProyecto: boolean;
  };
}
