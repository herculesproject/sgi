import { IRolSocio } from '@core/models/csp/rol-socio';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IRolSocioRequest } from './rol-socio-request';
import { I18N_FIELD_REQUEST_CONVERTER } from '@core/i18n/i18n-field.converter';

class RolSocioRequestConverter extends SgiBaseConverter<IRolSocioRequest, IRolSocio> {
  toTarget(value: IRolSocioRequest): IRolSocio {
    if (!value) {
      return value as unknown as IRolSocio;
    }
    return {
      id: undefined,
      nombre: value.nombre ? I18N_FIELD_REQUEST_CONVERTER.toTargetArray(value.nombre) : [],
      abreviatura: value.abreviatura,
      coordinador: value.coordinador,
      descripcion: value.descripcion,
      activo: true
    };
  }
  fromTarget(value: IRolSocio): IRolSocioRequest {
    if (!value) {
      return value as unknown as IRolSocioRequest;
    }
    return {
      nombre: value.nombre ? I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(value.nombre) : [],
      abreviatura: value.abreviatura,
      coordinador: value.coordinador,
      descripcion: value.descripcion,
    };
  }
}

export const ROL_SOCIO_REQUEST_CONVERTER = new RolSocioRequestConverter();
