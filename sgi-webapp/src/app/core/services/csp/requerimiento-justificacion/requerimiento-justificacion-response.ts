import { ITipoRequerimientoResponse } from "../tipo-requerimiento/tipo-requerimiento-response";

export interface IRequerimientoJustificacionResponse {
  id: number;
  proyectoProyectoSgeId: number;
  numRequerimiento: number;
  tipoRequerimiento: ITipoRequerimientoResponse;
  proyectoPeriodoJustificacionId: number;
  requerimientoPrevioId: number;
  fechaNotificacion: string;
  fechaFinAlegacion: string;
  observaciones: string;
  importeAceptadoCd: number;
  importeAceptadoCi: number;
  importeRechazadoCd: number;
  importeRechazadoCi: number;
  importeReintegrar: number;
  importeReintegrarCd: number;
  importeReintegrarCi: number;
  interesesReintegrar: number;
  importeAceptado: number;
  importeRechazado: number;
  subvencionJustificada: number;
  defectoSubvencion: number;
  anticipoJustificado: number;
  defectoAnticipo: number;
  recursoEstimado: boolean;
}
