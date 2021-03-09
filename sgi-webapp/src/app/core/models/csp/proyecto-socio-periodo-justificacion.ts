import { DateTime } from 'luxon';
import { IProyectoSocio } from './proyecto-socio';

export interface IProyectoSocioPeriodoJustificacion {
  id: number;
  proyectoSocio: IProyectoSocio;
  numPeriodo: number;
  fechaInicio: DateTime;
  fechaFin: DateTime;
  fechaInicioPresentacion: DateTime;
  fechaFinPresentacion: DateTime;
  observaciones: string;
  documentacionRecibida: boolean;
  fechaRecepcion: DateTime;
}
