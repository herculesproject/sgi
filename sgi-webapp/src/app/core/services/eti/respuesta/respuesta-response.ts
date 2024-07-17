import { ITipoDocumento } from '../../../models/eti/tipo-documento';
import { IApartadoResponse } from '../apartado/apartado-response';
import { IMemoriaResponse } from '../memoria/memoria-response';

export interface IRespuestaResponse {
  /** ID */
  id: number;
  /** Memoria */
  memoria: IMemoriaResponse;
  /** apartado */
  apartado: IApartadoResponse;
  /** Tipo de documento */
  tipoDocumento: ITipoDocumento;
  /** valor */
  valor: {
    [name: string]: any;
  };
}
