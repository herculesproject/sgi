import { I18nFieldValueResponse } from "@core/i18n/i18n-field-response";

export interface ITipoDocumentoResponse {
  id: number;
  nombre: I18nFieldValueResponse[];
  descripcion: string;
  padre: ITipoDocumentoResponse;
  activo: boolean;
}
