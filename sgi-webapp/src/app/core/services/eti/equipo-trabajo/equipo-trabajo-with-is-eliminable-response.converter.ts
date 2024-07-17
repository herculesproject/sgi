import { IEquipoTrabajoWithIsEliminable } from '@core/models/eti/equipo-trabajo-with-is-eliminable';
import { IPersona } from '@core/models/sgp/persona';
import { SgiBaseConverter } from '@sgi/framework/core';
import { PETICION_EVALUACION_RESPONSE_CONVERTER } from '../peticion-evaluacion/peticion-evaluacion-response.converter';
import { IEquipoTrabajoWithIsEliminableResponse } from './equipo-trabajo-with-is-eliminable-response';

class EquipoTrabajoWithIsEliminableResponseConverter
  extends SgiBaseConverter<IEquipoTrabajoWithIsEliminableResponse, IEquipoTrabajoWithIsEliminable> {
  toTarget(value: IEquipoTrabajoWithIsEliminableResponse): IEquipoTrabajoWithIsEliminable {
    if (!value) {
      return value as unknown as IEquipoTrabajoWithIsEliminable;
    }
    return {
      id: value.id,
      persona: { id: value.personaRef } as IPersona,
      peticionEvaluacion: PETICION_EVALUACION_RESPONSE_CONVERTER.toTarget(value.peticionEvaluacion),
      eliminable: value.eliminable
    };
  }

  fromTarget(value: IEquipoTrabajoWithIsEliminable): IEquipoTrabajoWithIsEliminableResponse {
    if (!value) {
      return value as unknown as IEquipoTrabajoWithIsEliminableResponse;
    }
    return {
      id: value.id,
      personaRef: value.persona?.id,
      peticionEvaluacion: PETICION_EVALUACION_RESPONSE_CONVERTER.fromTarget(value.peticionEvaluacion),
      eliminable: value.eliminable
    };
  }
}

export const EQUIPO_TRABAJO_WITH_IS_ELIMINABLE_RESPONSE_CONVERTER = new EquipoTrabajoWithIsEliminableResponseConverter();
