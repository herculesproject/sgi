import { ITipoDocumento } from '../tipos-configuracion';
import { ISolicitudBackend } from './solicitud-backend';

export interface ISolicitudDocumentoBackend {
  id: number;
  solicitud: ISolicitudBackend;
  comentario: string;
  documentoRef: string;
  nombre: string;
  tipoDocumento: ITipoDocumento;
}
