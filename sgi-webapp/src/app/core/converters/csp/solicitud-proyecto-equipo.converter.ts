import { ISolicitudProyectoEquipoBackend } from '@core/models/csp/backend/solicitud-proyecto-equipo-backend';
import { ISolicitudProyectoEquipo } from '@core/models/csp/solicitud-proyecto-equipo';
import { IPersona } from '@core/models/sgp/persona';
import { SgiBaseConverter } from '@sgi/framework/core';
import { SOLICITUD_PROYECTO_DATOS_CONVERTER } from './solicitud-proyecto-datos.converter';

class SolicitudProyectoEquipoConverter extends SgiBaseConverter<ISolicitudProyectoEquipoBackend, ISolicitudProyectoEquipo> {

  toTarget(value: ISolicitudProyectoEquipoBackend): ISolicitudProyectoEquipo {
    if (!value) {
      return value as unknown as ISolicitudProyectoEquipo;
    }
    return {
      id: value.id,
      mesFin: value.mesFin,
      mesInicio: value.mesInicio,
      persona: { personaRef: value.personaRef } as IPersona,
      rolProyecto: value.rolProyecto,
      solicitudProyectoDatos: SOLICITUD_PROYECTO_DATOS_CONVERTER.toTarget(value.solicitudProyectoDatos)
    };
  }

  fromTarget(value: ISolicitudProyectoEquipo): ISolicitudProyectoEquipoBackend {
    if (!value) {
      return value as unknown as ISolicitudProyectoEquipoBackend;
    }
    return {
      id: value.id,
      mesFin: value.mesFin,
      mesInicio: value.mesInicio,
      personaRef: value.persona.personaRef,
      rolProyecto: value.rolProyecto,
      solicitudProyectoDatos: SOLICITUD_PROYECTO_DATOS_CONVERTER.fromTarget(value.solicitudProyectoDatos)
    };
  }
}

export const SOLICITUD_PROYECTO_EQUIPO_CONVERTER = new SolicitudProyectoEquipoConverter();
