import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IGrupoDescriptor } from '@core/models/csp/grupo-descriptor';
import { ITipoDescriptorGrupo } from '@core/models/csp/tipo-descriptor-grupo';
import { SgiBaseConverter } from '@herculesproject/framework/core';
import { IGrupoDescriptorResponse } from './grupo-descriptor-response';

class GrupoDescriptorResponseConverter extends SgiBaseConverter<IGrupoDescriptorResponse, IGrupoDescriptor> {
  toTarget(value: IGrupoDescriptorResponse): IGrupoDescriptor {
    if (!value) {
      return value as unknown as IGrupoDescriptor;
    }
    return {
      id: value.id,
      grupoId: value.grupoId,
      tipoDescriptorGrupo: value.tipoDescriptorGrupoId ? { id: value.tipoDescriptorGrupoId } as ITipoDescriptorGrupo : undefined,
      texto: value.texto ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.texto) : []
    };
  }

  fromTarget(value: IGrupoDescriptor): IGrupoDescriptorResponse {
    if (!value) {
      return value as unknown as IGrupoDescriptorResponse;
    }
    return {
      id: value.id,
      grupoId: value.grupoId,
      tipoDescriptorGrupoId: value.tipoDescriptorGrupo?.id,
      texto: value.texto ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.texto) : []
    };
  }
}

export const GRUPO_DESCRIPTOR_RESPONSE_CONVERTER = new GrupoDescriptorResponseConverter();
