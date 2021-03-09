import { ISolicitudModalidadBackend } from '@core/models/csp/backend/solicitud-modalidad-backend';
import { ISolicitudModalidad } from '@core/models/csp/solicitud-modalidad';
import { IEmpresaEconomica } from '@core/models/sgp/empresa-economica';
import { SgiBaseConverter } from '@sgi/framework/core';
import { SOLICITUD_CONVERTER } from './solicitud.converter';

class SolicitudModalidadConverter extends SgiBaseConverter<ISolicitudModalidadBackend, ISolicitudModalidad> {

  toTarget(value: ISolicitudModalidadBackend): ISolicitudModalidad {
    if (!value) {
      return value as unknown as ISolicitudModalidad;
    }
    return {
      id: value.id,
      solicitud: SOLICITUD_CONVERTER.toTarget(value.solicitud),
      entidad: { personaRef: value.entidadRef } as IEmpresaEconomica,
      programa: value.programa
    };
  }

  fromTarget(value: ISolicitudModalidad): ISolicitudModalidadBackend {
    if (!value) {
      return value as unknown as ISolicitudModalidadBackend;
    }
    return {
      id: value.id,
      solicitud: SOLICITUD_CONVERTER.fromTarget(value.solicitud),
      entidadRef: value.entidad?.personaRef,
      programa: value.programa
    };
  }
}

export const SOLICITUD_MODALIDAD_CONVERTER = new SolicitudModalidadConverter();
