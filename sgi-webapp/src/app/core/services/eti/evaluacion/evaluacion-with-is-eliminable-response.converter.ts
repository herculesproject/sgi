import { IEvaluacionWithIsEliminable } from '@core/models/eti/evaluacion-with-is-eliminable';
import { IEvaluacionWithIsEliminableResponse } from '@core/services/eti/evaluacion/evaluacion-with-is-eliminable-response';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { CONVOCATORIA_REUNION_CONVERTER } from '../../../converters/eti/convocatoria-reunion.converter';
import { EVALUADOR_CONVERTER } from '../../../converters/eti/evaluador.converter';
import { COMITE_RESPONSE_CONVERTER } from '../comite/comite-response.converter';
import { MEMORIA_RESPONSE_CONVERTER } from '../memoria/memoria-response.converter';

class EvaluacionWithIsEliminableResponseConverter extends SgiBaseConverter<IEvaluacionWithIsEliminableResponse, IEvaluacionWithIsEliminable> {
  toTarget(value: IEvaluacionWithIsEliminableResponse): IEvaluacionWithIsEliminable {
    if (!value) {
      return value as unknown as IEvaluacionWithIsEliminable;
    }
    return {
      id: value.id,
      memoria: MEMORIA_RESPONSE_CONVERTER.toTarget(value.memoria),
      comite: COMITE_RESPONSE_CONVERTER.toTarget(value.comite),
      convocatoriaReunion: CONVOCATORIA_REUNION_CONVERTER.toTarget(value.convocatoriaReunion),
      tipoEvaluacion: value.tipoEvaluacion,
      version: value.version,
      dictamen: value.dictamen,
      evaluador1: EVALUADOR_CONVERTER.toTarget(value.evaluador1),
      evaluador2: EVALUADOR_CONVERTER.toTarget(value.evaluador2),
      fechaDictamen: LuxonUtils.fromBackend(value.fechaDictamen),
      esRevMinima: value.esRevMinima,
      activo: value.activo,
      comentario: value.comentario,
      eliminable: value.eliminable
    };
  }

  fromTarget(value: IEvaluacionWithIsEliminable): IEvaluacionWithIsEliminableResponse {
    if (!value) {
      return value as unknown as IEvaluacionWithIsEliminableResponse;
    }
    return {
      id: value.id,
      memoria: MEMORIA_RESPONSE_CONVERTER.fromTarget(value.memoria),
      comite: COMITE_RESPONSE_CONVERTER.fromTarget(value.comite),
      convocatoriaReunion: CONVOCATORIA_REUNION_CONVERTER.fromTarget(value.convocatoriaReunion),
      tipoEvaluacion: value.tipoEvaluacion,
      version: value.version,
      dictamen: value.dictamen,
      evaluador1: EVALUADOR_CONVERTER.fromTarget(value.evaluador1),
      evaluador2: EVALUADOR_CONVERTER.fromTarget(value.evaluador2),
      fechaDictamen: LuxonUtils.toBackend(value.fechaDictamen),
      esRevMinima: value.esRevMinima,
      activo: value.activo,
      comentario: value.comentario,
      eliminable: value.eliminable
    };
  }
}

export const EVALUACION_WITH_IS_ELIMINABLE_RESPONSE_CONVERTER = new EvaluacionWithIsEliminableResponseConverter();
