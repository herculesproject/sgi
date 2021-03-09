import { DateTime } from 'luxon';
import { ISolicitudProyectoSocio } from './solicitud-proyecto-socio';

export interface ISolicitudProyectoPeriodoJustificacion {
  id: number;
  solicitudProyectoSocio: ISolicitudProyectoSocio;
  numPeriodo: number;
  mesInicial: number;
  mesFinal: number;
  fechaInicio: DateTime;
  fechaFin: DateTime;
  observaciones: string;
}
