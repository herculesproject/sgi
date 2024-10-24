import { ITipoFaseResponse } from '@core/services/csp/tipo-fase/tipo-fase-response';
import { ITipoDocumento } from '../../../models/csp/tipos-configuracion';

export interface IProyectoDocumentoResponse {
  id: number;
  proyectoId: number;
  nombre: string;
  documentoRef: string;
  tipoFase: ITipoFaseResponse;
  tipoDocumento: ITipoDocumento;
  comentario: string;
  visible: boolean;
}
