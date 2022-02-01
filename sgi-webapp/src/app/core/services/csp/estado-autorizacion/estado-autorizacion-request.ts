import { Estado } from '@core/models/csp/estado-autorizacion';

export interface IEstadoAutorizacionRequest {
  autorizacionId: number;
  comentario: string;
  estado: Estado;
}
