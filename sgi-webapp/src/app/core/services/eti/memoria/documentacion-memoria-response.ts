import { ITipoDocumento } from '../../../models/eti/tipo-documento';
import { IMemoriaResponse } from './memoria-response';

export interface IDocumentacionMemoriaResponse {
  /** Id */
  id: number;
  /** Memoria */
  memoria: IMemoriaResponse;
  /** TIpo de documento */
  tipoDocumento: ITipoDocumento;
  /** Nombre */
  nombre: string;
  /** Ref del documento */
  documentoRef: string;
}
