import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { ITipoConfidencialidad } from '@core/models/csp/tipo-confidencialidad';
import { SgiBaseConverter } from '@herculesproject/framework/core';
import { ITipoConfidencialidadResponse } from './tipo-confidencialidad-response';

class TipoConfidencialidadResponseConverter extends SgiBaseConverter<ITipoConfidencialidadResponse, ITipoConfidencialidad> {
  toTarget(value: ITipoConfidencialidadResponse): ITipoConfidencialidad {
    if (!value) {
      return value as unknown as ITipoConfidencialidad;
    }
    return {
      id: value.id,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.nombre) : [],
      activo: value.activo
    };
  }
  fromTarget(value: ITipoConfidencialidad): ITipoConfidencialidadResponse {
    if (!value) {
      return value as unknown as ITipoConfidencialidadResponse;
    }
    return {
      id: value.id,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.nombre) : [],
      activo: value.activo
    };
  }
}

export const TIPO_CONFIDENCIALIDAD_RESPONSE_CONVERTER = new TipoConfidencialidadResponseConverter();
