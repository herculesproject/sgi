import { IActa } from '@core/models/eti/acta';
import { IActaResponse } from '@core/services/eti/acta/acta-response';
import { SgiBaseConverter } from '@sgi/framework/core';
import { CONVOCATORIA_REUNION_CONVERTER } from '../../../converters/eti/convocatoria-reunion.converter';

class ActaResponseConverter extends SgiBaseConverter<IActaResponse, IActa> {
  toTarget(value: IActaResponse): IActa {
    if (!value) {
      return value as unknown as IActa;
    }
    return {
      id: value.id,
      convocatoriaReunion: CONVOCATORIA_REUNION_CONVERTER.toTarget(value.convocatoriaReunion),
      horaInicio: value.horaInicio,
      minutoInicio: value.minutoInicio,
      horaFin: value.horaFin,
      minutoFin: value.minutoFin,
      resumen: value.resumen,
      numero: value.numero,
      inactiva: value.inactiva,
      activo: value.activo,
      estadoActual: value.estadoActual
    };
  }

  fromTarget(value: IActa): IActaResponse {
    if (!value) {
      return value as unknown as IActaResponse;
    }
    return {
      id: value.id,
      convocatoriaReunion: CONVOCATORIA_REUNION_CONVERTER.fromTarget(value.convocatoriaReunion),
      horaInicio: value.horaInicio,
      minutoInicio: value.minutoInicio,
      horaFin: value.horaFin,
      minutoFin: value.minutoFin,
      resumen: value.resumen,
      numero: value.numero,
      inactiva: value.inactiva,
      activo: value.activo,
      estadoActual: value.estadoActual
    };
  }
}

export const ACTA_RESPONSE_CONVERTER = new ActaResponseConverter();
