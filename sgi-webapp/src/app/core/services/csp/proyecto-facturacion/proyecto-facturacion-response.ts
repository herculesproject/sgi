import { IEstadoValidacionIP } from '@core/models/csp/estado-validacion-ip';
import { ITipoFacturacionResponse } from '../tipo-facturacion/tipo-facturacion-response';

export interface IProyectoFacturacionResponse {
  id: number;
  comentario: string;
  fechaConformidad: string;
  fechaEmision: string;
  importeBase: number;
  numeroPrevision: number;
  porcentajeIVA: number;
  proyectoId: number;
  tipoFacturacion: ITipoFacturacionResponse;
  estadoValidacionIP: IEstadoValidacionIP;
  proyectoProrrogaId: number;
  proyectoSgeRef: string;
}
