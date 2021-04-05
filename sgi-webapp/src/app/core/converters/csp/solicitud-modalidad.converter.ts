import { ISolicitudModalidadBackend } from '@core/models/csp/backend/solicitud-modalidad-backend';
import { ISolicitudModalidad } from '@core/models/csp/solicitud-modalidad';
import { IEmpresaEconomica } from '@core/models/sgp/empresa-economica';
import { SgiBaseConverter } from '@sgi/framework/core';

class SolicitudModalidadConverter extends SgiBaseConverter<ISolicitudModalidadBackend, ISolicitudModalidad> {

  toTarget(value: ISolicitudModalidadBackend): ISolicitudModalidad {
    if (!value) {
      return value as unknown as ISolicitudModalidad;
    }
    return {
      id: value.id,
      solicitudId: value.solicitudId,
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
      solicitudId: value.solicitudId,
      entidadRef: value.entidad?.personaRef,
      programa: value.programa
    };
  }
}

export const SOLICITUD_MODALIDAD_CONVERTER = new SolicitudModalidadConverter();
