import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { ITipoGrupo } from '@core/models/csp/tipo-grupo';
import { SgiBaseConverter } from '@herculesproject/framework/core';
import { ITipoGrupoResponse } from './tipo-grupo-response';

class TipoGrupoResponseConverter extends SgiBaseConverter<ITipoGrupoResponse, ITipoGrupo> {
  toTarget(value: ITipoGrupoResponse): ITipoGrupo {
    if (!value) {
      return value as unknown as ITipoGrupo;
    }
    return {
      id: value.id,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.nombre) : [],
      descripcion: value.descripcion ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.descripcion) : [],
      activo: value.activo
    };
  }
  fromTarget(value: ITipoGrupo): ITipoGrupoResponse {
    if (!value) {
      return value as unknown as ITipoGrupoResponse;
    }
    return {
      id: value.id,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.nombre) : [],
      descripcion: value.descripcion ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.descripcion) : [],
      activo: value.activo
    };
  }
}

export const TIPO_GRUPO_RESPONSE_CONVERTER = new TipoGrupoResponseConverter();
