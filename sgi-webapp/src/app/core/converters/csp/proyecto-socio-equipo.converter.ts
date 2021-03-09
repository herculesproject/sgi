import { IProyectoSocioEquipoBackend } from '@core/models/csp/backend/proyecto-socio-equipo-backend';
import { IProyectoSocioEquipo } from '@core/models/csp/proyecto-socio-equipo';
import { IPersona } from '@core/models/sgp/persona';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { PROYECTO_SOCIO_CONVERTER } from './proyecto-socio.converter';

class ProyectoSocioEquipoConverter extends SgiBaseConverter<IProyectoSocioEquipoBackend, IProyectoSocioEquipo> {

  toTarget(value: IProyectoSocioEquipoBackend): IProyectoSocioEquipo {
    if (!value) {
      return value as unknown as IProyectoSocioEquipo;
    }
    return {
      id: value.id,
      fechaFin: LuxonUtils.fromBackend(value.fechaFin),
      fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
      persona: { personaRef: value.personaRef } as IPersona,
      rolProyecto: value.rolProyecto,
      proyectoSocio: PROYECTO_SOCIO_CONVERTER.toTarget(value.proyectoSocio)
    };
  }

  fromTarget(value: IProyectoSocioEquipo): IProyectoSocioEquipoBackend {
    if (!value) {
      return value as unknown as IProyectoSocioEquipoBackend;
    }
    return {
      id: value.id,
      fechaFin: LuxonUtils.toBackend(value.fechaFin),
      fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
      personaRef: value.persona?.personaRef,
      rolProyecto: value.rolProyecto,
      proyectoSocio: PROYECTO_SOCIO_CONVERTER.fromTarget(value.proyectoSocio)
    };
  }
}

export const PROYECTO_SOCIO_EQUIPO_CONVERTER = new ProyectoSocioEquipoConverter();
