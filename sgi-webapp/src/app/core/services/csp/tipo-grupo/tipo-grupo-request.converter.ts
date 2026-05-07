import { I18N_FIELD_REQUEST_CONVERTER } from '@core/i18n/i18n-field.converter';
import { ITipoGrupo } from '@core/models/csp/tipo-grupo';
import { SgiBaseConverter } from '@herculesproject/framework/core';
import { ITipoGrupoRequest } from './tipo-grupo-request';

class TipoGrupoRequestConverter extends SgiBaseConverter<ITipoGrupoRequest, ITipoGrupo> {
  toTarget(value: ITipoGrupoRequest): ITipoGrupo {
    if (!value) {
      return value as unknown as ITipoGrupo;
    }
    return {
      id: undefined,
      nombre: value.nombre ? I18N_FIELD_REQUEST_CONVERTER.toTargetArray(value.nombre) : [],
      descripcion: value.descripcion ? I18N_FIELD_REQUEST_CONVERTER.toTargetArray(value.descripcion) : [],
      activo: true
    };
  }
  fromTarget(value: ITipoGrupo): ITipoGrupoRequest {
    if (!value) {
      return value as unknown as ITipoGrupoRequest;
    }
    return {
      nombre: value.nombre ? I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(value.nombre) : [],
      descripcion: value.descripcion ? I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(value.descripcion) : [],
    };
  }
}

export const TIPO_GRUPO_REQUEST_CONVERTER = new TipoGrupoRequestConverter();
