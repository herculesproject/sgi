import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { ITarea } from '@core/models/eti/tarea';
import { SgiBaseConverter } from '@sgi/framework/core';
import { EQUIPO_TRABAJO_RESPONSE_CONVERTER } from '../equipo-trabajo/equipo-trabajo-response.converter';
import { MEMORIA_RESPONSE_CONVERTER } from '../memoria/memoria-response.converter';
import { ITareaResponse } from './tarea-response';

class TareaResponseConverter extends SgiBaseConverter<ITareaResponse, ITarea> {
  toTarget(value: ITareaResponse): ITarea {
    if (!value) {
      return value as unknown as ITarea;
    }
    return {
      id: value.id,
      equipoTrabajo: EQUIPO_TRABAJO_RESPONSE_CONVERTER.toTarget(value.equipoTrabajo),
      memoria: MEMORIA_RESPONSE_CONVERTER.toTarget(value.memoria),
      formacionEspecifica: value.formacionEspecifica,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.nombre) : [],
      formacion: value.formacion,
      organismo: value.organismo,
      anio: value.anio,
      tipoTarea: value.tipoTarea
    };
  }

  fromTarget(value: ITarea): ITareaResponse {
    if (!value) {
      return value as unknown as ITareaResponse;
    }
    return {
      id: value.id,
      equipoTrabajo: EQUIPO_TRABAJO_RESPONSE_CONVERTER.fromTarget(value.equipoTrabajo),
      memoria: MEMORIA_RESPONSE_CONVERTER.fromTarget(value.memoria),
      formacionEspecifica: value.formacionEspecifica,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.nombre) : [],
      formacion: value.formacion,
      organismo: value.organismo,
      anio: value.anio,
      tipoTarea: value.tipoTarea
    };
  }
}

export const TAREA_RESPONSE_CONVERTER = new TareaResponseConverter();
