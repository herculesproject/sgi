import { I18nFieldValueResponse } from "@core/i18n/i18n-field-response";

export interface IConceptoGastoResponse {
  id: number;
  nombre: I18nFieldValueResponse[];
  descripcion: string;
  costesIndirectos: boolean;
  activo: boolean;
}
