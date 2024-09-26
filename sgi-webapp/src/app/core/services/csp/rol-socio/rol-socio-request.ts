import { I18nFieldValueRequest } from "@core/i18n/i18n-field-request";

export interface IRolSocioRequest {
  nombre: I18nFieldValueRequest[];
  abreviatura: string;
  coordinador: boolean;
  descripcion: I18nFieldValueRequest[];
}

