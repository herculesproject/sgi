import { IConvocatoriaSeguimientoCientificoBackend } from '@core/models/csp/backend/convocatoria-seguimiento-cientifico-backend';
import { IConvocatoriaSeguimientoCientifico } from '@core/models/csp/convocatoria-seguimiento-cientifico';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';

class ConvocatoriaSeguimientoCientificoConverter extends
  SgiBaseConverter<IConvocatoriaSeguimientoCientificoBackend, IConvocatoriaSeguimientoCientifico> {

  toTarget(value: IConvocatoriaSeguimientoCientificoBackend): IConvocatoriaSeguimientoCientifico {
    if (!value) {
      return value as unknown as IConvocatoriaSeguimientoCientifico;
    }
    return {
      id: value.id,
      numPeriodo: value.numPeriodo,
      mesInicial: value.mesInicial,
      mesFinal: value.mesFinal,
      fechaInicioPresentacion: LuxonUtils.fromBackend(value.fechaInicioPresentacion),
      fechaFinPresentacion: LuxonUtils.fromBackend(value.fechaFinPresentacion),
      observaciones: value.observaciones,
      convocatoria: value.convocatoria
    };
  }

  fromTarget(value: IConvocatoriaSeguimientoCientifico): IConvocatoriaSeguimientoCientificoBackend {
    if (!value) {
      return value as unknown as IConvocatoriaSeguimientoCientificoBackend;
    }
    return {
      id: value.id,
      numPeriodo: value.numPeriodo,
      mesInicial: value.mesInicial,
      mesFinal: value.mesFinal,
      fechaInicioPresentacion: LuxonUtils.toBackend(value.fechaInicioPresentacion),
      fechaFinPresentacion: LuxonUtils.toBackend(value.fechaFinPresentacion),
      observaciones: value.observaciones,
      convocatoria: value.convocatoria
    };
  }
}

export const CONVOCATORIA_SEGUIMIENTO_CIENTIFICO_CONVERTER = new ConvocatoriaSeguimientoCientificoConverter();
