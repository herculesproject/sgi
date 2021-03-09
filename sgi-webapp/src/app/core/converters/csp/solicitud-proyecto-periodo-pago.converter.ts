import { ISolicitudProyectoPeriodoPagoBackend } from '@core/models/csp/backend/solicitud-proyecto-periodo-pago-backend';
import { ISolicitudProyectoPeriodoPago } from '@core/models/csp/solicitud-proyecto-periodo-pago';
import { SgiBaseConverter } from '@sgi/framework/core';
import { SOLICITUD_PROYECTO_SOCIO_CONVERTER } from './solicitud-proyecto-socio.converter';

class SolicitudProyectoPeriodoPagoConverter extends SgiBaseConverter<ISolicitudProyectoPeriodoPagoBackend, ISolicitudProyectoPeriodoPago> {

  toTarget(value: ISolicitudProyectoPeriodoPagoBackend): ISolicitudProyectoPeriodoPago {
    if (!value) {
      return value as unknown as ISolicitudProyectoPeriodoPago;
    }
    return {
      id: value.id,
      solicitudProyectoSocio: SOLICITUD_PROYECTO_SOCIO_CONVERTER.toTarget(value.solicitudProyectoSocio),
      numPeriodo: value.numPeriodo,
      importe: value.importe,
      mes: value.mes
    };
  }

  fromTarget(value: ISolicitudProyectoPeriodoPago): ISolicitudProyectoPeriodoPagoBackend {
    if (!value) {
      return value as unknown as ISolicitudProyectoPeriodoPagoBackend;
    }
    return {
      id: value.id,
      solicitudProyectoSocio: SOLICITUD_PROYECTO_SOCIO_CONVERTER.fromTarget(value.solicitudProyectoSocio),
      numPeriodo: value.numPeriodo,
      importe: value.importe,
      mes: value.mes
    };
  }
}

export const SOLICITUD_PROYECTO_PERIODO_PAGO_CONVERTER = new SolicitudProyectoPeriodoPagoConverter();
