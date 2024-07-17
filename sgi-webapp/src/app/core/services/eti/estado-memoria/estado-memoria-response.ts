import { TipoEstadoMemoria } from "@core/models/eti/tipo-estado-memoria";
import { IMemoriaResponse } from "@core/services/eti/memoria/memoria-response";

export interface IEstadoMemoriaResponse {
  id: number;
  memoria: IMemoriaResponse;
  tipoEstadoMemoria: TipoEstadoMemoria;
  fechaEstado: string;
  comentario: string;
}
