import { I18nFieldValueResponse } from '@core/i18n/i18n-field-response';

export interface IGrupoDescriptorResponse {
  id: number;
  grupoId: number;
  tipoDescriptorGrupoId: number;
  texto: I18nFieldValueResponse[];
}
