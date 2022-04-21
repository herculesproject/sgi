
import { IProyectosCompetitivosPersona } from '@core/models/csp/proyectos-competitivos-persona';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IProyectosCompetitivosPersonaResponse } from './proyectos-competitivos-persona-response';

class ProyectosCompetitivosPersonaResponseConverter
  extends SgiBaseConverter<IProyectosCompetitivosPersonaResponse, IProyectosCompetitivosPersona> {
  toTarget(value: IProyectosCompetitivosPersonaResponse): IProyectosCompetitivosPersona {
    if (!value) {
      return value as unknown as IProyectosCompetitivosPersona;
    }
    return {
      numProyectosCompetitivos: value.numProyectosCompetitivos,
      numProyectosCompetitivosActuales: value.numProyectosCompetitivosActuales,
      numProyectosNoCompetitivos: value.numProyectosNoCompetitivos,
      numProyectosNoCompetitivosActuales: value.numProyectosNoCompetitivosActuales
    };
  }

  fromTarget(value: IProyectosCompetitivosPersona): IProyectosCompetitivosPersonaResponse {
    if (!value) {
      return value as unknown as IProyectosCompetitivosPersonaResponse;
    }
    return {
      numProyectosCompetitivos: value.numProyectosCompetitivos,
      numProyectosCompetitivosActuales: value.numProyectosCompetitivosActuales,
      numProyectosNoCompetitivos: value.numProyectosNoCompetitivos,
      numProyectosNoCompetitivosActuales: value.numProyectosNoCompetitivosActuales
    };
  }
}

export const PROYECTOS_COMPETITIVOS_PERSONA_RESPONSE_CONVERTER = new ProyectosCompetitivosPersonaResponseConverter();
