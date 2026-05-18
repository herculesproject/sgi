import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { ITipoDescriptorGrupo } from '@core/models/csp/tipo-descriptor-grupo';
import { SgiBaseConverter } from '@herculesproject/framework/core';
import { ITipoDescriptorGrupoResponse } from './tipo-descriptor-grupo-response';

class TipoDescriptorGrupoResponseConverter extends SgiBaseConverter<ITipoDescriptorGrupoResponse, ITipoDescriptorGrupo> {
  toTarget(value: ITipoDescriptorGrupoResponse): ITipoDescriptorGrupo {
    if (!value) {
      return value as unknown as ITipoDescriptorGrupo;
    }
    return {
      id: value.id,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.nombre) : [],
      activo: value.activo
    };
  }
  fromTarget(value: ITipoDescriptorGrupo): ITipoDescriptorGrupoResponse {
    if (!value) {
      return value as unknown as ITipoDescriptorGrupoResponse;
    }
    return {
      id: value.id,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.nombre) : [],
      activo: value.activo
    };
  }
}

export const TIPO_DESCRIPTOR_GRUPO_RESPONSE_CONVERTER = new TipoDescriptorGrupoResponseConverter();
