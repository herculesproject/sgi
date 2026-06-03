import { IGrupoUnidadVinculacion } from '@core/models/csp/grupo-unidad-vinculacion';
import { IUnidadVinculacion } from '@core/models/sgo/unidad-vinculacion';
import { SgiBaseConverter } from '@herculesproject/framework/core';
import { IGrupoUnidadVinculacionResponse } from './grupo-unidad-vinculacion-response';

class IGrupoUnidadVinculacionResponseConverter extends SgiBaseConverter<IGrupoUnidadVinculacionResponse, IGrupoUnidadVinculacion> {

  toTarget(value: IGrupoUnidadVinculacionResponse): IGrupoUnidadVinculacion {
    if (!value) {
      return value as unknown as IGrupoUnidadVinculacion;
    }

    return {
      id: value.id,
      grupoId: value.grupoId,
      unidadVinculacion: { id: value.unidadVinculacionRef } as IUnidadVinculacion
    };
  }

  fromTarget(value: IGrupoUnidadVinculacion): IGrupoUnidadVinculacionResponse {
    if (!value) {
      return value as unknown as IGrupoUnidadVinculacionResponse;
    }

    return {
      id: value.id,
      grupoId: value.grupoId,
      unidadVinculacionRef: value.unidadVinculacion?.id
    };
  }
}

export const GRUPO_UNIDAD_VINCULACION_RESPONSE_CONVERTER = new IGrupoUnidadVinculacionResponseConverter();
