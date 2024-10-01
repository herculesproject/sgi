import { I18nFieldValueResponse } from "@core/i18n/i18n-field-response";

export interface IFuenteFinanciacionResponse {
  id: number;
  nombre: string;
  descripcion: string;
  fondoEstructural: boolean;
  tipoAmbitoGeografico: {
    id: number;
    nombre: I18nFieldValueResponse[];
  };
  tipoOrigenFuenteFinanciacion: {
    id: number;
    nombre: string;
  };
  activo: boolean;
}
