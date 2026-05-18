import { I18nFieldValue } from '@core/i18n/i18n-field';
import { ITipoDescriptorGrupo } from './tipo-descriptor-grupo';

export interface IGrupoDescriptor {
  id: number;
  grupoId: number;
  tipoDescriptorGrupo: ITipoDescriptorGrupo;
  texto: I18nFieldValue[];
}
