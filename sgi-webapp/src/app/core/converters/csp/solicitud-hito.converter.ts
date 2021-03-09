import { ISolicitudHitoBackend } from '@core/models/csp/backend/solicitud-hito-backend';
import { ISolicitudHito } from '@core/models/csp/solicitud-hito';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { SOLICITUD_CONVERTER } from './solicitud.converter';

class SolicitudHitoConverter extends SgiBaseConverter<ISolicitudHitoBackend, ISolicitudHito> {

  toTarget(value: ISolicitudHitoBackend): ISolicitudHito {
    if (!value) {
      return value as unknown as ISolicitudHito;
    }
    return {
      id: value.id,
      solicitud: SOLICITUD_CONVERTER.toTarget(value.solicitud),
      fecha: LuxonUtils.fromBackend(value.fecha),
      comentario: value.comentario,
      generaAviso: value.generaAviso,
      tipoHito: value.tipoHito
    };
  }

  fromTarget(value: ISolicitudHito): ISolicitudHitoBackend {
    if (!value) {
      return value as unknown as ISolicitudHitoBackend;
    }
    return {
      id: value.id,
      solicitud: SOLICITUD_CONVERTER.fromTarget(value.solicitud),
      fecha: LuxonUtils.toBackend(value.fecha),
      comentario: value.comentario,
      generaAviso: value.generaAviso,
      tipoHito: value.tipoHito
    };
  }
}

export const SOLICITUD_HITO_CONVERTER = new SolicitudHitoConverter();
