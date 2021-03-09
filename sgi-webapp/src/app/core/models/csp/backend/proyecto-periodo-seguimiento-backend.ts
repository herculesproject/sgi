import { IProyectoBackend } from './proyecto-backend';

export interface IProyectoPeriodoSeguimientoBackend {
  id: number;
  proyecto: IProyectoBackend;
  numPeriodo: number;
  fechaInicio: string;
  fechaFin: string;
  fechaInicioPresentacion: string;
  fechaFinPresentacion: string;
  observaciones: string;
}
