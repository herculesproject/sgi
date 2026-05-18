import { I18nFieldValueRequest } from '@core/i18n/i18n-field-request';

export interface IGrupoDescriptorRequest {
  grupoId: number;
  tipoDescriptorGrupoId: number;
  texto: I18nFieldValueRequest[];
}
