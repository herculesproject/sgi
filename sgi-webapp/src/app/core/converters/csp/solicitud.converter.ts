import { ISolicitudBackend } from '@core/models/csp/backend/solicitud-backend';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { ISolicitud } from '@core/models/csp/solicitud';
import { IPersona } from '@core/models/sgp/persona';
import { IUnidadGestion } from '@core/models/usr/unidad-gestion';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ESTADO_SOLICITUD_CONVERTER } from './estado-solicitud.converter';

class SolicitudConverter extends SgiBaseConverter<ISolicitudBackend, ISolicitud> {

  toTarget(value: ISolicitudBackend): ISolicitud {
    if (!value) {
      return value as unknown as ISolicitud;
    }
    return {
      id: value.id,
      activo: value.activo,
      codigoExterno: value.codigoExterno,
      codigoRegistroInterno: value.codigoRegistroInterno,
      estado: ESTADO_SOLICITUD_CONVERTER.toTarget(value.estado),
      convocatoria: value.convocatoria,
      convocatoriaExterna: value.convocatoriaExterna,
      creador: { personaRef: value.creadorRef } as IPersona,
      solicitante: { personaRef: value.solicitanteRef } as IPersona,
      formularioSolicitud: value.formularioSolicitud,
      unidadGestion: { acronimo: value.unidadGestionRef } as IUnidadGestion,
      observaciones: value.observaciones
    };
  }

  fromTarget(value: ISolicitud): ISolicitudBackend {
    if (!value) {
      return value as unknown as ISolicitudBackend;
    }
    return {
      id: value.id,
      activo: value.activo,
      codigoExterno: value.codigoExterno,
      codigoRegistroInterno: value.codigoRegistroInterno,
      estado: ESTADO_SOLICITUD_CONVERTER.fromTarget(value.estado),
      convocatoria: value.convocatoria ? { id: value.convocatoria.id } as IConvocatoria : undefined,
      convocatoriaExterna: value.convocatoriaExterna,
      creadorRef: value.creador?.personaRef,
      solicitanteRef: value.solicitante?.personaRef,
      formularioSolicitud: value.formularioSolicitud,
      unidadGestionRef: value.unidadGestion?.acronimo,
      observaciones: value.observaciones
    };
  }
}

export const SOLICITUD_CONVERTER = new SolicitudConverter();
