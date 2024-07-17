import { IEquipoTrabajo } from '@core/models/eti/equipo-trabajo';
import { IPersona } from '@core/models/sgp/persona';
import { SgiBaseConverter } from '@sgi/framework/core';
import { PETICION_EVALUACION_RESPONSE_CONVERTER } from '../peticion-evaluacion/peticion-evaluacion-response.converter';
import { IEquipoTrabajoResponse } from './equipo-trabajo-response';

class EquipoTrabajoResponseConverter extends SgiBaseConverter<IEquipoTrabajoResponse, IEquipoTrabajo> {
  toTarget(value: IEquipoTrabajoResponse): IEquipoTrabajo {
    if (!value) {
      return value as unknown as IEquipoTrabajo;
    }
    return {
      id: value.id,
      persona: { id: value.personaRef } as IPersona,
      peticionEvaluacion: PETICION_EVALUACION_RESPONSE_CONVERTER.toTarget(value.peticionEvaluacion)
    };
  }

  fromTarget(value: IEquipoTrabajo): IEquipoTrabajoResponse {
    if (!value) {
      return value as unknown as IEquipoTrabajoResponse;
    }
    return {
      id: value.id,
      personaRef: value.persona?.id,
      peticionEvaluacion: PETICION_EVALUACION_RESPONSE_CONVERTER.fromTarget(value.peticionEvaluacion)
    };
  }
}

export const EQUIPO_TRABAJO_RESPONSE_CONVERTER = new EquipoTrabajoResponseConverter();
