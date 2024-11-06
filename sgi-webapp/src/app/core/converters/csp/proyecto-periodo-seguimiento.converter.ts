import { IProyectoPeriodoSeguimiento } from '@core/models/csp/proyecto-periodo-seguimiento';
import { IProyectoPeriodoSeguimientoResponse } from '@core/services/csp/proyecto-periodo-seguimiento/proyecto-periodo-seguimiento-response';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';

class ProyectoPeriodoSeguimientoConverter extends SgiBaseConverter<IProyectoPeriodoSeguimientoResponse, IProyectoPeriodoSeguimiento> {

  toTarget(value: IProyectoPeriodoSeguimientoResponse): IProyectoPeriodoSeguimiento {
    if (!value) {
      return value as unknown as IProyectoPeriodoSeguimiento;
    }
    return {
      id: value.id,
      proyectoId: value.proyectoId,
      numPeriodo: value.numPeriodo,
      fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
      fechaFin: LuxonUtils.fromBackend(value.fechaFin),
      fechaInicioPresentacion: LuxonUtils.fromBackend(value.fechaInicioPresentacion),
      fechaFinPresentacion: LuxonUtils.fromBackend(value.fechaFinPresentacion),
      tipoSeguimiento: value.tipoSeguimiento,
      observaciones: value.observaciones,
      convocatoriaPeriodoSeguimientoId: value.convocatoriaPeriodoSeguimientoId,
      fechaPresentacionDocumentacion: LuxonUtils.fromBackend(value.fechaPresentacionDocumentacion)
    };
  }

  fromTarget(value: IProyectoPeriodoSeguimiento): IProyectoPeriodoSeguimientoResponse {
    if (!value) {
      return value as unknown as IProyectoPeriodoSeguimientoResponse;
    }
    return {
      id: value.id,
      proyectoId: value.proyectoId,
      numPeriodo: value.numPeriodo,
      fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
      fechaFin: LuxonUtils.toBackend(value.fechaFin),
      fechaInicioPresentacion: LuxonUtils.toBackend(value.fechaInicioPresentacion),
      fechaFinPresentacion: LuxonUtils.toBackend(value.fechaFinPresentacion),
      tipoSeguimiento: value.tipoSeguimiento,
      observaciones: value.observaciones,
      convocatoriaPeriodoSeguimientoId: value.convocatoriaPeriodoSeguimientoId,
      fechaPresentacionDocumentacion: LuxonUtils.toBackend(value.fechaPresentacionDocumentacion)
    };
  }
}

export const PROYECTO_PERIODO_SEGUIMIENTO_CONVERTER = new ProyectoPeriodoSeguimientoConverter();
