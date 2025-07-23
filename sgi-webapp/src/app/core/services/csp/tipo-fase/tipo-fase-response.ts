import { I18nFieldValueResponse } from "@core/i18n/i18n-field-response";

export interface ITipoFaseResponse {
  id: number;
  nombre: I18nFieldValueResponse[];
  descripcion: string;
  activo: boolean;
}
