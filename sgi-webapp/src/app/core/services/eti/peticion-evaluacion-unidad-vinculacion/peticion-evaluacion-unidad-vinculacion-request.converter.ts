import { IPeticionEvaluacionUnidadVinculacion } from '@core/models/eti/peticion-evaluacion-unidad-vinculacion';
import { IUnidadVinculacion } from '@core/models/sgo/unidad-vinculacion';
import { SgiBaseConverter } from '@herculesproject/framework/core';
import { IPeticionEvaluacionUnidadVinculacionRequest } from './peticion-evaluacion-unidad-vinculacion-request';

class PeticionEvaluacionUnidadVinculacionRequestConverter
  extends SgiBaseConverter<IPeticionEvaluacionUnidadVinculacionRequest, IPeticionEvaluacionUnidadVinculacion> {

  toTarget(value: IPeticionEvaluacionUnidadVinculacionRequest): IPeticionEvaluacionUnidadVinculacion {
    if (!value) {
      return value as unknown as IPeticionEvaluacionUnidadVinculacion;
    }

    return {
      id: undefined,
      peticionEvaluacionId: undefined,
      unidadVinculacion: { id: value.unidadVinculacionRef } as IUnidadVinculacion
    };
  }

  fromTarget(value: IPeticionEvaluacionUnidadVinculacion): IPeticionEvaluacionUnidadVinculacionRequest {
    if (!value) {
      return value as unknown as IPeticionEvaluacionUnidadVinculacionRequest;
    }

    return {
      unidadVinculacionRef: value.unidadVinculacion?.id
    };
  }
}

export const PETICION_EVALUACION_UNIDAD_VINCULACION_REQUEST_CONVERTER = new PeticionEvaluacionUnidadVinculacionRequestConverter();
