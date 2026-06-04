import { IProyectoUnidadVinculacion } from '@core/models/csp/proyecto-unidad-vinculacion';
import { IUnidadVinculacion } from '@core/models/sgo/unidad-vinculacion';
import { SgiBaseConverter } from '@herculesproject/framework/core';
import { IProyectoUnidadVinculacionRequest } from './proyecto-unidad-vinculacion-request';

class IProyectoUnidadVinculacionRequestConverter extends SgiBaseConverter<IProyectoUnidadVinculacionRequest, IProyectoUnidadVinculacion> {

  toTarget(value: IProyectoUnidadVinculacionRequest): IProyectoUnidadVinculacion {
    if (!value) {
      return value as unknown as IProyectoUnidadVinculacion;
    }

    return {
      id: undefined,
      proyectoId: value.proyectoId,
      unidadVinculacion: { id: value.unidadVinculacionRef } as IUnidadVinculacion
    };
  }

  fromTarget(value: IProyectoUnidadVinculacion): IProyectoUnidadVinculacionRequest {
    if (!value) {
      return value as unknown as IProyectoUnidadVinculacionRequest;
    }

    return {
      proyectoId: value.proyectoId,
      unidadVinculacionRef: value.unidadVinculacion?.id
    };
  }
}

export const PROYECTO_UNIDAD_VINCULACION_REQUEST_CONVERTER = new IProyectoUnidadVinculacionRequestConverter();
