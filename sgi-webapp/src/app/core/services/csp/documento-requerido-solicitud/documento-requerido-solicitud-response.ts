import { ITipoDocumentoResponse } from '../tipo-documento/tipo-documento-response';

export interface IDocumentoRequeridoSolicitudResponse {
  id: number;
  configuracionSolicitudId: number;
  tipoDocumento: ITipoDocumentoResponse;
  observaciones: string;
}
