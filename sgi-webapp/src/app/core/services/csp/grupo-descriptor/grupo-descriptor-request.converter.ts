import { I18N_FIELD_REQUEST_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IGrupoDescriptor } from '@core/models/csp/grupo-descriptor';
import { ITipoDescriptorGrupo } from '@core/models/csp/tipo-descriptor-grupo';
import { SgiBaseConverter } from '@herculesproject/framework/core';
import { IGrupoDescriptorRequest } from './grupo-descriptor-request';

class GrupoDescriptorRequestConverter extends SgiBaseConverter<IGrupoDescriptorRequest, IGrupoDescriptor> {
  toTarget(value: IGrupoDescriptorRequest): IGrupoDescriptor {
    if (!value) {
      return value as unknown as IGrupoDescriptor;
    }
    return {
      id: undefined,
      grupoId: value.grupoId,
      tipoDescriptorGrupo: value.tipoDescriptorGrupoId ? { id: value.tipoDescriptorGrupoId } as ITipoDescriptorGrupo : undefined,
      texto: value.texto ? I18N_FIELD_REQUEST_CONVERTER.toTargetArray(value.texto) : []
    };
  }

  fromTarget(value: IGrupoDescriptor): IGrupoDescriptorRequest {
    if (!value) {
      return value as unknown as IGrupoDescriptorRequest;
    }
    return {
      grupoId: value.grupoId,
      tipoDescriptorGrupoId: value.tipoDescriptorGrupo?.id,
      texto: value.texto ? I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(value.texto) : []
    };
  }
}

export const GRUPO_DESCRIPTOR_REQUEST_CONVERTER = new GrupoDescriptorRequestConverter();
