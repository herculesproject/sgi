import { IProyectoUnidadVinculacion } from '@core/models/csp/proyecto-unidad-vinculacion';
import { IUnidadVinculacion } from '@core/models/sgo/unidad-vinculacion';
import { SgiBaseConverter } from '@herculesproject/framework/core';
import { IProyectoUnidadVinculacionResponse } from './proyecto-unidad-vinculacion-response';

class IProyectoUnidadVinculacionResponseConverter extends SgiBaseConverter<IProyectoUnidadVinculacionResponse, IProyectoUnidadVinculacion> {

  toTarget(value: IProyectoUnidadVinculacionResponse): IProyectoUnidadVinculacion {
    if (!value) {
      return value as unknown as IProyectoUnidadVinculacion;
    }

    return {
      id: value.id,
      proyectoId: value.proyectoId,
      unidadVinculacion: { id: value.unidadVinculacionRef } as IUnidadVinculacion
    };
  }

  fromTarget(value: IProyectoUnidadVinculacion): IProyectoUnidadVinculacionResponse {
    if (!value) {
      return value as unknown as IProyectoUnidadVinculacionResponse;
    }

    return {
      id: value.id,
      proyectoId: value.proyectoId,
      unidadVinculacionRef: value.unidadVinculacion?.id
    };
  }
}

export const PROYECTO_UNIDAD_VINCULACION_RESPONSE_CONVERTER = new IProyectoUnidadVinculacionResponseConverter();
