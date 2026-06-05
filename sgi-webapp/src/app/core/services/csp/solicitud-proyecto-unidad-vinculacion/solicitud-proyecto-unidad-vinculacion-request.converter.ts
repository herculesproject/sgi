import { ISolicitudProyectoUnidadVinculacion } from '@core/models/csp/solicitud-proyecto-unidad-vinculacion';
import { IUnidadVinculacion } from '@core/models/sgo/unidad-vinculacion';
import { SgiBaseConverter } from '@herculesproject/framework/core';
import { ISolicitudProyectoUnidadVinculacionRequest } from './solicitud-proyecto-unidad-vinculacion-request';

class ISolicitudProyectoUnidadVinculacionRequestConverter
  extends SgiBaseConverter<ISolicitudProyectoUnidadVinculacionRequest, ISolicitudProyectoUnidadVinculacion> {

  toTarget(value: ISolicitudProyectoUnidadVinculacionRequest): ISolicitudProyectoUnidadVinculacion {
    if (!value) {
      return value as unknown as ISolicitudProyectoUnidadVinculacion;
    }

    return {
      id: undefined,
      solicitudProyectoId: undefined,
      unidadVinculacion: { id: value.unidadVinculacionRef } as IUnidadVinculacion
    };
  }

  fromTarget(value: ISolicitudProyectoUnidadVinculacion): ISolicitudProyectoUnidadVinculacionRequest {
    if (!value) {
      return value as unknown as ISolicitudProyectoUnidadVinculacionRequest;
    }

    return {
      unidadVinculacionRef: value.unidadVinculacion?.id
    };
  }
}

export const SOLICITUD_PROYECTO_UNIDAD_VINCULACION_REQUEST_CONVERTER =
  new ISolicitudProyectoUnidadVinculacionRequestConverter();
