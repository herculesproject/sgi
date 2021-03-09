import { IProyectoSocioBackend } from './proyecto-socio-backend';

export interface IProyectoSocioPeriodoPagoBackend {
  id: number;
  proyectoSocio: IProyectoSocioBackend;
  numPeriodo: number;
  importe: number;
  fechaPrevistaPago: string;
  fechaPago: string;
}
