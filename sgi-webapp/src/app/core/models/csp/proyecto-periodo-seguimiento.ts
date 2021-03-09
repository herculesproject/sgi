import { DateTime } from 'luxon';
import { IProyecto } from './proyecto';

export interface IProyectoPeriodoSeguimiento {
  id: number;
  proyecto: IProyecto;
  numPeriodo: number;
  fechaInicio: DateTime;
  fechaFin: DateTime;
  fechaInicioPresentacion: DateTime;
  fechaFinPresentacion: DateTime;
  observaciones: string;
}
