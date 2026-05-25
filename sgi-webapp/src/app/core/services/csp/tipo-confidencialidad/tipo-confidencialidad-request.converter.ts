import { I18N_FIELD_REQUEST_CONVERTER } from '@core/i18n/i18n-field.converter';
import { ITipoConfidencialidad } from '@core/models/csp/tipo-confidencialidad';
import { SgiBaseConverter } from '@herculesproject/framework/core';
import { ITipoConfidencialidadRequest } from './tipo-confidencialidad-request';

class TipoConfidencialidadRequestConverter extends SgiBaseConverter<ITipoConfidencialidadRequest, ITipoConfidencialidad> {
  toTarget(value: ITipoConfidencialidadRequest): ITipoConfidencialidad {
    if (!value) {
      return value as unknown as ITipoConfidencialidad;
    }
    return {
      id: undefined,
      nombre: value.nombre ? I18N_FIELD_REQUEST_CONVERTER.toTargetArray(value.nombre) : [],
      activo: true
    };
  }
  fromTarget(value: ITipoConfidencialidad): ITipoConfidencialidadRequest {
    if (!value) {
      return value as unknown as ITipoConfidencialidadRequest;
    }
    return {
      nombre: value.nombre ? I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(value.nombre) : [],
    };
  }
}

export const TIPO_CONFIDENCIALIDAD_REQUEST_CONVERTER = new TipoConfidencialidadRequestConverter();
