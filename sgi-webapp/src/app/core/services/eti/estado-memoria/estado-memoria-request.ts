import { TipoEstadoMemoria } from '../../../models/eti/tipo-estado-memoria';
import { IMemoriaBackend } from '../memoria/memoria-response';

export interface IEstadoMemoriaRequest {
  id: number;
  memoria: IMemoriaBackend;
  tipoEstadoMemoria: TipoEstadoMemoria;
  fechaEstado: string;
  comentario: string;
}
