import { ITipoFaseResponse } from '@core/services/csp/tipo-fase/tipo-fase-response';
import { ITipoDocumentoResponse } from '../tipo-documento/tipo-documento-response';

export interface IProyectoDocumentoResponse {
  id: number;
  proyectoId: number;
  nombre: string;
  documentoRef: string;
  tipoFase: ITipoFaseResponse;
  tipoDocumento: ITipoDocumentoResponse;
  comentario: string;
  visible: boolean;
}
