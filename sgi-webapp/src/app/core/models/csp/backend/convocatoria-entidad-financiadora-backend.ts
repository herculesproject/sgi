import { ITipoFinanciacionResponse } from '@core/services/csp/tipo-financiacion/tipo-financiacion-response';
import { IFuenteFinanciacion } from '../fuente-financiacion';

export interface IConvocatoriaEntidadFinanciadoraBackend {
  id: number;
  entidadRef: string;
  convocatoriaId: number;
  fuenteFinanciacion: IFuenteFinanciacion;
  tipoFinanciacion: ITipoFinanciacionResponse;
  porcentajeFinanciacion: number;
  importeFinanciacion: number;
}
