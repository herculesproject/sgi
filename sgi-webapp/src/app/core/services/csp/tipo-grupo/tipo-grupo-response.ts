import { I18nFieldValueResponse } from '@core/i18n/i18n-field-response';

export interface ITipoGrupoResponse {
  id: number;
  nombre: I18nFieldValueResponse[];
  descripcion: I18nFieldValueResponse[];
  activo: boolean;
}
