import { Estado } from '@core/models/csp/estado-autorizacion';

export interface IEstadoAutorizacionRequest {
  autorizacionId: number;
  cometario: string;
  fecha: string;
  estado: Estado;
}
