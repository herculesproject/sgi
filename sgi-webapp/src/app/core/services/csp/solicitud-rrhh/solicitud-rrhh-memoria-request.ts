import { I18nFieldValueRequest } from "@core/i18n/i18n-field-request";

export interface ISolicitudRrhhMemoriaRequest {
  tituloTrabajo: I18nFieldValueRequest[];
  resumen: string;
  observaciones: string;
}
