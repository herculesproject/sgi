import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { ITareaWithIsEliminable } from '@core/models/eti/tarea-with-is-eliminable';
import { ITareaWithIsEliminableResponse } from '@core/services/eti/tarea/tarea-with-is-eliminable-response';
import { SgiBaseConverter } from '@sgi/framework/core';
import { EQUIPO_TRABAJO_RESPONSE_CONVERTER } from '../equipo-trabajo/equipo-trabajo-response.converter';
import { MEMORIA_RESPONSE_CONVERTER } from '../memoria/memoria-response.converter';

class TareaWithIsEliminableResponseConverter extends SgiBaseConverter<ITareaWithIsEliminableResponse, ITareaWithIsEliminable> {
  toTarget(value: ITareaWithIsEliminableResponse): ITareaWithIsEliminable {
    if (!value) {
      return value as unknown as ITareaWithIsEliminable;
    }
    return {
      id: value.id,
      equipoTrabajo: EQUIPO_TRABAJO_RESPONSE_CONVERTER.toTarget(value.equipoTrabajo),
      memoria: MEMORIA_RESPONSE_CONVERTER.toTarget(value.memoria),
      formacionEspecifica: value.formacionEspecifica,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.nombre) : [],
      formacion: value.formacion ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.formacion) : [],
      organismo: value.organismo,
      anio: value.anio,
      tipoTarea: value.tipoTarea,
      eliminable: value.eliminable
    };
  }

  fromTarget(value: ITareaWithIsEliminable): ITareaWithIsEliminableResponse {
    if (!value) {
      return value as unknown as ITareaWithIsEliminableResponse;
    }
    return {
      id: value.id,
      equipoTrabajo: EQUIPO_TRABAJO_RESPONSE_CONVERTER.fromTarget(value.equipoTrabajo),
      memoria: MEMORIA_RESPONSE_CONVERTER.fromTarget(value.memoria),
      formacionEspecifica: value.formacionEspecifica,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.nombre) : [],
      formacion: value.formacion ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.formacion) : [],
      organismo: value.organismo,
      anio: value.anio,
      tipoTarea: value.tipoTarea,
      eliminable: value.eliminable
    };
  }
}

export const TAREA_WITH_IS_ELIMINABLE_RESPONSE_CONVERTER = new TareaWithIsEliminableResponseConverter();
