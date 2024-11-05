import { ITipoFinanciacionResponse } from '@core/services/csp/tipo-financiacion/tipo-financiacion-response';
import { IFuenteFinanciacion } from '../fuente-financiacion';

export interface ISolicitudProyectoEntidadFinanciadoraAjenaBackend {
  id: number;
  entidadRef: string;
  solicitudProyectoId: number;
  fuenteFinanciacion: IFuenteFinanciacion;
  tipoFinanciacion: ITipoFinanciacionResponse;
  porcentajeFinanciacion: number;
  importeFinanciacion: number;
}
