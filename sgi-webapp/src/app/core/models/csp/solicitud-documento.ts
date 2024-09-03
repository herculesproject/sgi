import { ITipoDocumento, ITipoFase } from './tipos-configuracion';

export interface ISolicitudDocumento {
  id: number;
  solicitudId: number;
  comentario: string;
  documentoRef: string;
  nombre: string;
  tipoDocumento: ITipoDocumento;
  tipoFase: ITipoFase;
}
