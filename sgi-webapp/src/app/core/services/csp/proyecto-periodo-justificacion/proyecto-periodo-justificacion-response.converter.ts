import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoPeriodoJustificacion } from '@core/models/csp/proyecto-periodo-justificacion';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@herculesproject/framework/core';
import { IProyectoPeriodoJustificacionResponse } from './proyecto-periodo-justificacion-response';

class ProyectoPeriodojustificacionResponseConverter extends
  SgiBaseConverter<IProyectoPeriodoJustificacionResponse, IProyectoPeriodoJustificacion> {
  toTarget(value: IProyectoPeriodoJustificacionResponse): IProyectoPeriodoJustificacion {
    if (!value) {
      return value as unknown as IProyectoPeriodoJustificacion;
    }
    return {
      id: value.id,
      proyecto: value.proyectoId ? {
        id: value.proyectoId
      } as IProyecto : null,
      numPeriodo: value.numPeriodo,
      fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
      fechaFin: LuxonUtils.fromBackend(value.fechaFin),
      fechaInicioPresentacion: LuxonUtils.fromBackend(value.fechaInicioPresentacion),
      fechaFinPresentacion: LuxonUtils.fromBackend(value.fechaFinPresentacion),
      tipoJustificacion: value.tipoJustificacion,
      observaciones: value.observaciones ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.observaciones) : [],
      convocatoriaPeriodoJustificacionId: value.convocatoriaPeriodoJustificacionId,
      fechaPresentacionJustificacion: LuxonUtils.fromBackend(value.fechaPresentacionJustificacion),
      identificadorJustificacion: value.identificadorJustificacion,
      estado: value.estado
    };
  }
  fromTarget(value: IProyectoPeriodoJustificacion): IProyectoPeriodoJustificacionResponse {
    if (!value) {
      return value as unknown as IProyectoPeriodoJustificacionResponse;
    }
    return {
      id: value.id,
      proyectoId: value.proyecto.id,
      numPeriodo: value.numPeriodo,
      fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
      fechaFin: LuxonUtils.toBackend(value.fechaFin),
      fechaInicioPresentacion: LuxonUtils.toBackend(value.fechaInicioPresentacion),
      fechaFinPresentacion: LuxonUtils.toBackend(value.fechaFinPresentacion),
      tipoJustificacion: value.tipoJustificacion,
      observaciones: value.observaciones ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.observaciones) : [],
      convocatoriaPeriodoJustificacionId: value.convocatoriaPeriodoJustificacionId,
      fechaPresentacionJustificacion: LuxonUtils.toBackend(value.fechaPresentacionJustificacion),
      identificadorJustificacion: value.identificadorJustificacion,
      estado: value.estado
    };
  }
}

export const PROYECTO_PERIODO_JUSTIFICACION_RESPONSE_CONVERTER = new ProyectoPeriodojustificacionResponseConverter();
