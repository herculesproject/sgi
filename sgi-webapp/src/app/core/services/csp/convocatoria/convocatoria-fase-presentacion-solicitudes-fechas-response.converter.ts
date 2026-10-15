import { IConvocatoriaFasePresentacionSolicitudesFechas } from '@core/models/csp/convocatoria-fase-presentacion-solicitudes-fechas';
import { IConvocatoriaFasePresentacionSolicitudesFechasResponse } from '@core/services/csp/convocatoria/convocatoria-fase-presentacion-solicitudes-fechas-response';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@herculesproject/framework/core';

class ConvocatoriaFasePresentacionSolicitudesFechasResponseConverter
  extends SgiBaseConverter<IConvocatoriaFasePresentacionSolicitudesFechasResponse, IConvocatoriaFasePresentacionSolicitudesFechas> {
  toTarget(value: IConvocatoriaFasePresentacionSolicitudesFechasResponse): IConvocatoriaFasePresentacionSolicitudesFechas {
    if (!value) {
      return value as unknown as IConvocatoriaFasePresentacionSolicitudesFechas;
    }
    return {
      fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
      fechaFin: LuxonUtils.fromBackend(value.fechaFin)
    };
  }

  fromTarget(value: IConvocatoriaFasePresentacionSolicitudesFechas): IConvocatoriaFasePresentacionSolicitudesFechasResponse {
    if (!value) {
      return value as unknown as IConvocatoriaFasePresentacionSolicitudesFechasResponse;
    }
    return {
      fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
      fechaFin: LuxonUtils.toBackend(value.fechaFin)
    };
  }
}

export const CONVOCATORIA_FASE_PRESENTACION_SOLICITUDES_FECHAS_RESPONSE_CONVERTER =
  new ConvocatoriaFasePresentacionSolicitudesFechasResponseConverter();
