import { DateTime } from 'luxon';
import { IProyecto } from './proyecto';

export interface IProyectoPaqueteTrabajo {
  /** Id */
  id: number;
  /** Proyecto */
  proyecto: IProyecto;
  /** nombre */
  nombre: string;
  /** fechaInicio */
  fechaInicio: DateTime;
  /** fechaFin */
  fechaFin: DateTime;
  /** personaMes */
  personaMes: number;
  /** descripcion */
  descripcion: string;
}
