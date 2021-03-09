import { IProyectoSocioBackend } from './proyecto-socio-backend';

export interface IProyectoSocioPeriodoJustificacionBackend {
  id: number;
  proyectoSocio: IProyectoSocioBackend;
  numPeriodo: number;
  fechaInicio: string;
  fechaFin: string;
  fechaInicioPresentacion: string;
  fechaFinPresentacion: string;
  observaciones: string;
  documentacionRecibida: boolean;
  fechaRecepcion: string;
}
