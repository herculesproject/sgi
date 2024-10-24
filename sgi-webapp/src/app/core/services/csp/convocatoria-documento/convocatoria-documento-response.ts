import { ITipoDocumento } from '../../../models/csp/tipos-configuracion';
import { ITipoFaseResponse } from '../tipo-fase/tipo-fase-response';

export interface IConvocatoriaDocumentoResponse {
  id: number;
  convocatoriaId: number;
  nombre: string;
  documentoRef: string;
  tipoFase: ITipoFaseResponse;
  tipoDocumento: ITipoDocumento;
  publico: boolean;
  observaciones: string;
}
