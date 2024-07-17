
import { IAuditoriaBackend } from '../../../models/eti/backend/auditoria-backend';
import { IEvaluacionResponse } from '../evaluacion/evaluacion-response';
import { TipoEstadoComentario } from '../../../models/eti/comentario';
import { TipoComentario } from '../../../models/eti/tipo-comentario';
import { IApartadoResponse } from '../apartado/apartado-response';
import { IMemoriaResponse } from '../memoria/memoria-response';

export interface IComentarioResponse extends IAuditoriaBackend {
  /** Id */
  id: number;
  /** Memoria */
  memoria: IMemoriaResponse;
  /** Apartado del formulario */
  apartado: IApartadoResponse;
  /** Evaluación */
  evaluacion: IEvaluacionResponse;
  /** Tipo de comentario */
  tipoComentario: TipoComentario;
  /** Texto */
  texto: string;
  /** Estado */
  estado: TipoEstadoComentario;
  /** Fecha estado */
  fechaEstado: string;
}
