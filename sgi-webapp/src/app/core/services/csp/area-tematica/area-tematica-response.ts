import { I18nFieldValueResponse } from "@core/i18n/i18n-field-response";

export interface IAreaTematicaResponse {
  id: number;
  nombre: I18nFieldValueResponse[];
  descripcion: string;
  padre: IAreaTematicaResponse;
  activo: boolean;
}

