import { ISolicitudProyectoEquipoSocioBackend } from '@core/models/csp/backend/solicitud-proyecto-equipo-socio-backend';
import { ISolicitudProyectoEquipoSocio } from '@core/models/csp/solicitud-proyecto-equipo-socio';
import { IPersona } from '@core/models/sgp/persona';
import { SgiBaseConverter } from '@sgi/framework/core';
import { SOLICITUD_PROYECTO_SOCIO_CONVERTER } from './solicitud-proyecto-socio.converter';

class SolicitudProyectoEquipoSocioConverter extends SgiBaseConverter<ISolicitudProyectoEquipoSocioBackend, ISolicitudProyectoEquipoSocio> {

  toTarget(value: ISolicitudProyectoEquipoSocioBackend): ISolicitudProyectoEquipoSocio {
    if (!value) {
      return value as unknown as ISolicitudProyectoEquipoSocio;
    }
    return {
      id: value.id,
      mesFin: value.mesFin,
      mesInicio: value.mesInicio,
      persona: { personaRef: value.personaRef } as IPersona,
      rolProyecto: value.rolProyecto,
      solicitudProyectoSocio: SOLICITUD_PROYECTO_SOCIO_CONVERTER.toTarget(value.solicitudProyectoSocio)
    };
  }

  fromTarget(value: ISolicitudProyectoEquipoSocio): ISolicitudProyectoEquipoSocioBackend {
    if (!value) {
      return value as unknown as ISolicitudProyectoEquipoSocioBackend;
    }
    return {
      id: value.id,
      mesFin: value.mesFin,
      mesInicio: value.mesInicio,
      personaRef: value.persona.personaRef,
      rolProyecto: value.rolProyecto,
      solicitudProyectoSocio: SOLICITUD_PROYECTO_SOCIO_CONVERTER.fromTarget(value.solicitudProyectoSocio)
    };
  }
}

export const SOLICITUD_PROYECTO_EQUIPO_SOCIO_CONVERTER = new SolicitudProyectoEquipoSocioConverter();
