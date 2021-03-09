import { IProyectoBackend } from './proyecto-backend';

export interface IProyectoPaqueteTrabajoBackend {
  /** Id */
  id: number;
  /** Proyecto */
  proyecto: IProyectoBackend;
  /** nombre */
  nombre: string;
  /** fechaInicio */
  fechaInicio: string;
  /** fechaFin */
  fechaFin: string;
  /** personaMes */
  personaMes: number;
  /** descripcion */
  descripcion: string;
}
