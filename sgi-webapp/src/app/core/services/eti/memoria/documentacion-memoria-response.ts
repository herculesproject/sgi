import { ITipoDocumentoResponse } from '../tipo-documento/tipo-documento-response';
import { IMemoriaResponse } from './memoria-response';

export interface IDocumentacionMemoriaResponse {
  /** Id */
  id: number;
  /** Memoria */
  memoria: IMemoriaResponse;
  /** TIpo de documento */
  tipoDocumento: ITipoDocumentoResponse;
  /** Nombre */
  nombre: string;
  /** Ref del documento */
  documentoRef: string;
}
