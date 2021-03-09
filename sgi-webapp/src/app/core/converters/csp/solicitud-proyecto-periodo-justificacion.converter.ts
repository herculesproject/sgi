import { ISolicitudProyectoPeriodoJustificacionBackend } from '@core/models/csp/backend/solicitud-proyecto-periodo-justificacion-backend';
import { ISolicitudProyectoPeriodoJustificacion } from '@core/models/csp/solicitud-proyecto-periodo-justificacion';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { SOLICITUD_PROYECTO_SOCIO_CONVERTER } from './solicitud-proyecto-socio.converter';

class SolicitudProyectoPeriodoJustificacionConverter extends
  SgiBaseConverter<ISolicitudProyectoPeriodoJustificacionBackend, ISolicitudProyectoPeriodoJustificacion> {

  toTarget(value: ISolicitudProyectoPeriodoJustificacionBackend): ISolicitudProyectoPeriodoJustificacion {
    if (!value) {
      return value as unknown as ISolicitudProyectoPeriodoJustificacion;
    }
    return {
      id: value.id,
      solicitudProyectoSocio: SOLICITUD_PROYECTO_SOCIO_CONVERTER.toTarget(value.solicitudProyectoSocio),
      numPeriodo: value.numPeriodo,
      mesInicial: value.mesInicial,
      mesFinal: value.mesFinal,
      fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
      fechaFin: LuxonUtils.fromBackend(value.fechaFin),
      observaciones: value.observaciones
    };
  }

  fromTarget(value: ISolicitudProyectoPeriodoJustificacion): ISolicitudProyectoPeriodoJustificacionBackend {
    if (!value) {
      return value as unknown as ISolicitudProyectoPeriodoJustificacionBackend;
    }
    return {
      id: value.id,
      solicitudProyectoSocio: SOLICITUD_PROYECTO_SOCIO_CONVERTER.fromTarget(value.solicitudProyectoSocio),
      numPeriodo: value.numPeriodo,
      mesInicial: value.mesInicial,
      mesFinal: value.mesFinal,
      fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
      fechaFin: LuxonUtils.toBackend(value.fechaFin),
      observaciones: value.observaciones
    };
  }
}

export const SOLICITUD_PROYECTO_PERIODO_JUSTIFICACION_CONVERTER = new SolicitudProyectoPeriodoJustificacionConverter();
