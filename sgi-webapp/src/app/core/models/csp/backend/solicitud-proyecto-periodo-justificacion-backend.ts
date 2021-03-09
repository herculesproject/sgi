import { ISolicitudProyectoSocioBackend } from './solicitud-proyecto-socio-backend';

export interface ISolicitudProyectoPeriodoJustificacionBackend {
  id: number;
  solicitudProyectoSocio: ISolicitudProyectoSocioBackend;
  numPeriodo: number;
  mesInicial: number;
  mesFinal: number;
  fechaInicio: string;
  fechaFin: string;
  observaciones: string;
}
