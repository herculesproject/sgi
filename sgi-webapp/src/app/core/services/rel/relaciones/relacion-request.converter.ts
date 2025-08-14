import { I18N_FIELD_REQUEST_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { IProyecto } from '@core/models/csp/proyecto';
import { IInvencion } from '@core/models/pii/invencion';
import { IRelacion } from '@core/models/rel/relacion';
import { SgiBaseConverter } from '@herculesproject/framework/core';
import { IRelacionRequest } from './relacion-request';

class RelacionRequestConverter extends SgiBaseConverter<IRelacionRequest, IRelacion> {
  toTarget(value: IRelacionRequest): IRelacion {
    if (!value) {
      return value as unknown as IRelacion;
    }
    return {
      id: undefined,
      tipoEntidadDestino: value.tipoEntidadDestino,
      tipoEntidadOrigen: value.tipoEntidadOrigen,
      entidadDestino: { id: +value.entidadDestinoRef } as IInvencion | IProyecto | IConvocatoria,
      entidadOrigen: { id: +value.entidadOrigenRef } as IInvencion | IProyecto | IConvocatoria,
      observaciones: value.observaciones ? I18N_FIELD_REQUEST_CONVERTER.toTargetArray(value.observaciones) : []
    };
  }
  fromTarget(value: IRelacion): IRelacionRequest {
    if (!value) {
      return value as unknown as IRelacionRequest;
    }
    return {
      tipoEntidadDestino: value.tipoEntidadDestino,
      tipoEntidadOrigen: value.tipoEntidadOrigen,
      entidadDestinoRef: value?.entidadDestino?.id?.toString(),
      entidadOrigenRef: value?.entidadOrigen?.id?.toString(),
      observaciones: value.observaciones ? I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(value.observaciones) : []
    };
  }
}

export const RELACION_REQUEST_CONVERTER = new RelacionRequestConverter();