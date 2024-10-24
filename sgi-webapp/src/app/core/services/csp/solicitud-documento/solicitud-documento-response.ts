import { ITipoDocumento } from '../../../models/csp/tipos-configuracion';
import { ITipoFaseResponse } from '../tipo-fase/tipo-fase-response';

export interface ISolicitudDocumentoResponse {
  id: number;
  solicitudId: number;
  comentario: string;
  documentoRef: string;
  nombre: string;
  tipoDocumento: ITipoDocumento;
  tipoFase: ITipoFaseResponse;
}
