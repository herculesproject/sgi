import { ITipoDocumentoResponse } from '../tipo-documento/tipo-documento-response';
import { ITipoFaseResponse } from '../tipo-fase/tipo-fase-response';

export interface ISolicitudDocumentoResponse {
  id: number;
  solicitudId: number;
  comentario: string;
  documentoRef: string;
  nombre: string;
  tipoDocumento: ITipoDocumentoResponse;
  tipoFase: ITipoFaseResponse;
}
