import { ISolicitudProyectoUnidadVinculacion } from '@core/models/csp/solicitud-proyecto-unidad-vinculacion';
import { IUnidadVinculacion } from '@core/models/sgo/unidad-vinculacion';
import { SgiBaseConverter } from '@herculesproject/framework/core';
import { ISolicitudProyectoUnidadVinculacionResponse } from './solicitud-proyecto-unidad-vinculacion-response';

class ISolicitudProyectoUnidadVinculacionResponseConverter
  extends SgiBaseConverter<ISolicitudProyectoUnidadVinculacionResponse, ISolicitudProyectoUnidadVinculacion> {

  toTarget(value: ISolicitudProyectoUnidadVinculacionResponse): ISolicitudProyectoUnidadVinculacion {
    if (!value) {
      return value as unknown as ISolicitudProyectoUnidadVinculacion;
    }

    return {
      id: value.id,
      solicitudProyectoId: undefined,
      unidadVinculacion: { id: value.unidadVinculacionRef } as IUnidadVinculacion
    };
  }

  fromTarget(value: ISolicitudProyectoUnidadVinculacion): ISolicitudProyectoUnidadVinculacionResponse {
    if (!value) {
      return value as unknown as ISolicitudProyectoUnidadVinculacionResponse;
    }

    return {
      id: value.id,
      unidadVinculacionRef: value.unidadVinculacion?.id
    };
  }
}

export const SOLICITUD_PROYECTO_UNIDAD_VINCULACION_RESPONSE_CONVERTER =
  new ISolicitudProyectoUnidadVinculacionResponseConverter();
