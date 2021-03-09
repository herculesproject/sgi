import { ITipoDocumento } from '../tipos-configuracion';
import { IConfiguracionSolicitudBackend } from './configuracion-solicitud-backend';

export interface IDocumentoRequeridoBackend {
  id: number;
  configuracionSolicitud: IConfiguracionSolicitudBackend;
  tipoDocumento: ITipoDocumento;
  observaciones: string;
}
