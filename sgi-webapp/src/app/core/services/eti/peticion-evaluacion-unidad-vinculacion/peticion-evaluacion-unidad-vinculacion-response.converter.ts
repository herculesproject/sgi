import { IPeticionEvaluacionUnidadVinculacion } from '@core/models/eti/peticion-evaluacion-unidad-vinculacion';
import { IUnidadVinculacion } from '@core/models/sgo/unidad-vinculacion';
import { SgiBaseConverter } from '@herculesproject/framework/core';
import { IPeticionEvaluacionUnidadVinculacionResponse } from './peticion-evaluacion-unidad-vinculacion-response';

class PeticionEvaluacionUnidadVinculacionResponseConverter
  extends SgiBaseConverter<IPeticionEvaluacionUnidadVinculacionResponse, IPeticionEvaluacionUnidadVinculacion> {

  toTarget(value: IPeticionEvaluacionUnidadVinculacionResponse): IPeticionEvaluacionUnidadVinculacion {
    if (!value) {
      return value as unknown as IPeticionEvaluacionUnidadVinculacion;
    }

    return {
      id: value.id,
      peticionEvaluacionId: value.peticionEvaluacionId,
      unidadVinculacion: { id: value.unidadVinculacionRef } as IUnidadVinculacion
    };
  }

  fromTarget(value: IPeticionEvaluacionUnidadVinculacion): IPeticionEvaluacionUnidadVinculacionResponse {
    if (!value) {
      return value as unknown as IPeticionEvaluacionUnidadVinculacionResponse;
    }

    return {
      id: value.id,
      peticionEvaluacionId: value.peticionEvaluacionId,
      unidadVinculacionRef: value.unidadVinculacion?.id
    };
  }
}

export const PETICION_EVALUACION_UNIDAD_VINCULACION_RESPONSE_CONVERTER = new PeticionEvaluacionUnidadVinculacionResponseConverter();
