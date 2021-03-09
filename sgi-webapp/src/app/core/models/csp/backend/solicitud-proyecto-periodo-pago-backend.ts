import { ISolicitudProyectoSocioBackend } from './solicitud-proyecto-socio-backend';

export interface ISolicitudProyectoPeriodoPagoBackend {
  id: number;
  solicitudProyectoSocio: ISolicitudProyectoSocioBackend;
  numPeriodo: number;
  importe: number;
  mes: number;
}
