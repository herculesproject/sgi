import { ITipoFinanciacionResponse } from '@core/services/csp/tipo-financiacion/tipo-financiacion-response';
import { IFuenteFinanciacion } from '../fuente-financiacion';

export interface IProyectoEntidadFinanciadoraBackend {
  id: number;
  entidadRef: string;
  proyectoId: number;
  fuenteFinanciacion: IFuenteFinanciacion;
  tipoFinanciacion: ITipoFinanciacionResponse;
  porcentajeFinanciacion: number;
  importeFinanciacion: number;
  ajena: boolean;
}
