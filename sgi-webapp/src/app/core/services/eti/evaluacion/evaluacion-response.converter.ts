import { IEvaluacion } from '@core/models/eti/evaluacion';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { CONVOCATORIA_REUNION_CONVERTER } from '../../../converters/eti/convocatoria-reunion.converter';
import { EVALUADOR_CONVERTER } from '../../../converters/eti/evaluador.converter';
import { MEMORIA_RESPONSE_CONVERTER } from '../memoria/memoria-response.converter';
import { IEvaluacionResponse } from './evaluacion-response';

class EvaluacionResponseConverter extends SgiBaseConverter<IEvaluacionResponse, IEvaluacion> {
  toTarget(value: IEvaluacionResponse): IEvaluacion {
    if (!value) {
      return value as unknown as IEvaluacion;
    }
    return {
      id: value.id,
      memoria: MEMORIA_RESPONSE_CONVERTER.toTarget(value.memoria),
      comite: value.comite,
      convocatoriaReunion: CONVOCATORIA_REUNION_CONVERTER.toTarget(value.convocatoriaReunion),
      tipoEvaluacion: value.tipoEvaluacion,
      version: value.version,
      dictamen: value.dictamen,
      evaluador1: EVALUADOR_CONVERTER.toTarget(value.evaluador1),
      evaluador2: EVALUADOR_CONVERTER.toTarget(value.evaluador2),
      fechaDictamen: LuxonUtils.fromBackend(value.fechaDictamen),
      esRevMinima: value.esRevMinima,
      comentario: value.comentario,
      activo: value.activo
    };
  }

  fromTarget(value: IEvaluacion): IEvaluacionResponse {
    if (!value) {
      return value as unknown as IEvaluacionResponse;
    }
    return {
      id: value.id,
      memoria: MEMORIA_RESPONSE_CONVERTER.fromTarget(value.memoria),
      comite: value.comite,
      convocatoriaReunion: CONVOCATORIA_REUNION_CONVERTER.fromTarget(value.convocatoriaReunion),
      tipoEvaluacion: value.tipoEvaluacion,
      version: value.version,
      dictamen: value.dictamen,
      evaluador1: EVALUADOR_CONVERTER.fromTarget(value.evaluador1),
      evaluador2: EVALUADOR_CONVERTER.fromTarget(value.evaluador2),
      fechaDictamen: LuxonUtils.toBackend(value.fechaDictamen),
      esRevMinima: value.esRevMinima,
      comentario: value.comentario,
      activo: value.activo
    };
  }
}

export const EVALUACION_RESPONSE_CONVERTER = new EvaluacionResponseConverter();
