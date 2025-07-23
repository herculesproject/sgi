import { I18nFieldValueRequest } from "@core/i18n/i18n-field-request";

export interface IFacturaPrevistaRequest {
  proyectoIdSGI: number;
  proyectoSgeId: string;
  numeroPrevision: number;
  fechaEmision: string;
  importeBase: number;
  porcentajeIVA: number;
  comentario: string;
  tipoFacturacion: I18nFieldValueRequest[];
}
