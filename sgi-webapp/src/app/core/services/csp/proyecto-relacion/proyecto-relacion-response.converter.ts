import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IProyectoRelacion } from '@core/models/csp/proyecto-relacion';
import { SgiBaseConverter } from '@herculesproject/framework/core';
import { IProyectoRelacionResponse } from './proyecto-relacion-response';

class ProyectoRelacionResponseConverter extends SgiBaseConverter<IProyectoRelacionResponse, IProyectoRelacion> {
  toTarget(value: IProyectoRelacionResponse): IProyectoRelacion {
    if (!value) {
      return value as unknown as IProyectoRelacion;
    }
    return {
      id: value.id,
      tipoEntidadRelacionada: value.tipoEntidadRelacionada,
      entidadRelacionada: value.entidadRelacionada ? {
        id: value.entidadRelacionada.id,
        titulo: value.entidadRelacionada.titulo
          ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.entidadRelacionada.titulo) : [],
        codigoExterno: value.entidadRelacionada.codigoExterno,
        codigosSge: value.entidadRelacionada.codigosSge
      } : null,
      observaciones: value.observaciones ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.observaciones) : []
    };
  }

  fromTarget(value: IProyectoRelacion): IProyectoRelacionResponse {
    if (!value) {
      return value as unknown as IProyectoRelacionResponse;
    }
    return {
      id: value.id,
      tipoEntidadRelacionada: value.tipoEntidadRelacionada,
      entidadRelacionada: value.entidadRelacionada ? {
        id: value.entidadRelacionada.id,
        titulo: value.entidadRelacionada.titulo
          ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.entidadRelacionada.titulo) : [],
        codigoExterno: value.entidadRelacionada.codigoExterno,
        codigosSge: value.entidadRelacionada.codigosSge
      } : null,
      observaciones: value.observaciones ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.observaciones) : []
    };
  }
}

export const PROYECTO_RELACION_RESPONSE_CONVERTER = new ProyectoRelacionResponseConverter();
