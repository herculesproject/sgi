import { IEvaluadorBackend } from '@core/models/eti/backend/evaluador-backend';
import { IEvaluador } from '@core/models/eti/evaluador';
import { IPersona } from '@core/models/sgp/persona';
import { COMITE_RESPONSE_CONVERTER } from '@core/services/eti/comite/comite-response.converter';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';

class EvaluadorConverter extends SgiBaseConverter<IEvaluadorBackend, IEvaluador> {
  toTarget(value: IEvaluadorBackend): IEvaluador {
    if (!value) {
      return value as unknown as IEvaluador;
    }
    return {
      id: value.id,
      comite: COMITE_RESPONSE_CONVERTER.toTarget(value.comite),
      cargoComite: value.cargoComite,
      resumen: value.resumen,
      fechaAlta: LuxonUtils.fromBackend(value.fechaAlta),
      fechaBaja: LuxonUtils.fromBackend(value.fechaBaja),
      persona: { id: value.personaRef } as IPersona,
      activo: value.activo
    };
  }

  fromTarget(value: IEvaluador): IEvaluadorBackend {
    if (!value) {
      return value as unknown as IEvaluadorBackend;
    }
    return {
      id: value.id,
      comite: COMITE_RESPONSE_CONVERTER.fromTarget(value.comite),
      cargoComite: value.cargoComite,
      resumen: value.resumen,
      fechaAlta: LuxonUtils.toBackend(value.fechaAlta),
      fechaBaja: LuxonUtils.toBackend(value.fechaBaja),
      personaRef: value.persona?.id,
      activo: value.activo
    };
  }
}

export const EVALUADOR_CONVERTER = new EvaluadorConverter();
