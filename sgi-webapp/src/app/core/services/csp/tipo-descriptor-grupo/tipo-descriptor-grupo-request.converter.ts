import { I18N_FIELD_REQUEST_CONVERTER } from '@core/i18n/i18n-field.converter';
import { ITipoDescriptorGrupo } from '@core/models/csp/tipo-descriptor-grupo';
import { SgiBaseConverter } from '@herculesproject/framework/core';
import { ITipoDescriptorGrupoRequest } from './tipo-descriptor-grupo-request';

class TipoDescriptorGrupoRequestConverter extends SgiBaseConverter<ITipoDescriptorGrupoRequest, ITipoDescriptorGrupo> {
  toTarget(value: ITipoDescriptorGrupoRequest): ITipoDescriptorGrupo {
    if (!value) {
      return value as unknown as ITipoDescriptorGrupo;
    }
    return {
      id: undefined,
      nombre: value.nombre ? I18N_FIELD_REQUEST_CONVERTER.toTargetArray(value.nombre) : [],
      activo: true
    };
  }
  fromTarget(value: ITipoDescriptorGrupo): ITipoDescriptorGrupoRequest {
    if (!value) {
      return value as unknown as ITipoDescriptorGrupoRequest;
    }
    return {
      nombre: value.nombre ? I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(value.nombre) : [],
    };
  }
}

export const TIPO_DESCRIPTOR_GRUPO_REQUEST_CONVERTER = new TipoDescriptorGrupoRequestConverter();
