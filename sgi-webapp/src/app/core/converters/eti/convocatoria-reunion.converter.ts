import { IConvocatoriaReunionBackend } from '@core/models/eti/backend/convocatoria-reunion-backend';
import { IConvocatoriaReunion } from '@core/models/eti/convocatoria-reunion';
import { COMITE_RESPONSE_CONVERTER } from '@core/services/eti/comite/comite-response.converter';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';

class ConvocatoriaReunionConverter extends SgiBaseConverter<IConvocatoriaReunionBackend, IConvocatoriaReunion> {
  toTarget(value: IConvocatoriaReunionBackend): IConvocatoriaReunion {
    if (!value) {
      return value as unknown as IConvocatoriaReunion;
    }
    return {
      id: value.id,
      comite: COMITE_RESPONSE_CONVERTER.toTarget(value.comite),
      tipoConvocatoriaReunion: value.tipoConvocatoriaReunion,
      fechaEvaluacion: LuxonUtils.fromBackend(value.fechaEvaluacion),
      horaInicio: value.horaInicio,
      minutoInicio: value.minutoInicio,
      horaInicioSegunda: value.horaInicioSegunda,
      minutoInicioSegunda: value.minutoInicioSegunda,
      fechaLimite: LuxonUtils.fromBackend(value.fechaLimite),
      videoconferencia: value.videoconferencia,
      lugar: value.lugar,
      ordenDia: value.ordenDia,
      anio: value.anio,
      numeroActa: value.numeroActa,
      fechaEnvio: LuxonUtils.fromBackend(value.fechaEnvio),
      activo: value.activo,
      codigo: `ACTA${value.numeroActa}/${LuxonUtils.fromBackend(value.fechaEvaluacion).year}/${value.comite.codigo}`
    };
  }

  fromTarget(value: IConvocatoriaReunion): IConvocatoriaReunionBackend {
    if (!value) {
      return value as unknown as IConvocatoriaReunionBackend;
    }
    return {
      id: value.id,
      comite: COMITE_RESPONSE_CONVERTER.fromTarget(value.comite),
      tipoConvocatoriaReunion: value.tipoConvocatoriaReunion,
      fechaEvaluacion: LuxonUtils.toBackend(value.fechaEvaluacion),
      horaInicio: value.horaInicio,
      minutoInicio: value.minutoInicio,
      horaInicioSegunda: value.horaInicioSegunda,
      minutoInicioSegunda: value.minutoInicioSegunda,
      fechaLimite: LuxonUtils.toBackend(value.fechaLimite),
      videoconferencia: value.videoconferencia,
      lugar: value.lugar,
      ordenDia: value.ordenDia,
      anio: value.anio,
      numeroActa: value.numeroActa,
      fechaEnvio: LuxonUtils.toBackend(value.fechaEnvio),
      activo: value.activo
    };
  }
}

export const CONVOCATORIA_REUNION_CONVERTER = new ConvocatoriaReunionConverter();
