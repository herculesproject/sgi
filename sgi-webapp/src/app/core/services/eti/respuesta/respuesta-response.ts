import { ITipoDocumento } from '../../../models/eti/tipo-documento';

export interface IRespuestaResponse {
  /** ID */
  id: number;
  /** Memoria */
  memoriaId: number;
  /** apartado */
  apartadoId: number;
  /** Tipo de documento */
  tipoDocumento: ITipoDocumento;
  /** valor */
  valor: {
    [name: string]: any;
  };
}
