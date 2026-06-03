import { IGrupoUnidadVinculacion } from '@core/models/csp/grupo-unidad-vinculacion';
import { IUnidadVinculacion } from '@core/models/sgo/unidad-vinculacion';
import { SgiBaseConverter } from '@herculesproject/framework/core';
import { IGrupoUnidadVinculacionRequest } from './grupo-unidad-vinculacion-request';

class IGrupoUnidadVinculacionRequestConverter extends SgiBaseConverter<IGrupoUnidadVinculacionRequest, IGrupoUnidadVinculacion> {

  toTarget(value: IGrupoUnidadVinculacionRequest): IGrupoUnidadVinculacion {
    if (!value) {
      return value as unknown as IGrupoUnidadVinculacion;
    }

    return {
      id: undefined,
      grupoId: value.grupoId,
      unidadVinculacion: { id: value.unidadVinculacionRef } as IUnidadVinculacion
    };
  }

  fromTarget(value: IGrupoUnidadVinculacion): IGrupoUnidadVinculacionRequest {
    if (!value) {
      return value as unknown as IGrupoUnidadVinculacionRequest;
    }

    return {
      grupoId: value.grupoId,
      unidadVinculacionRef: value.unidadVinculacion?.id
    };
  }
}

export const GRUPO_UNIDAD_VINCULACION_REQUEST_CONVERTER = new IGrupoUnidadVinculacionRequestConverter();
