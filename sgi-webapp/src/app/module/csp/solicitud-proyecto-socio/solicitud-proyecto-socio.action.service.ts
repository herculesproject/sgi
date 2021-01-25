import { Injectable } from '@angular/core';
import { ActionService } from '@core/services/action-service';
import { SolicitudProyectoSocioDatosGeneralesFragment } from './solicitud-proyecto-socio-formulario/solicitud-proyecto-socio-datos-generales/solicitud-proyecto-socio-datos-generales.fragment';
import { ISolicitudProyectoSocio } from '@core/models/csp/solicitud-proyecto-socio';
import { NGXLogger } from 'ngx-logger';
import { SolicitudProyectoSocioService } from '@core/services/csp/solicitud-proyecto-socio.service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { SolicitudProyectoPeriodoPagoService } from '@core/services/csp/solicitud-proyecto-periodo-pago.service';
import { SolicitudProyectoSocioPeriodoPagoFragment } from './solicitud-proyecto-socio-formulario/solicitud-proyecto-socio-periodo-pago/solicitud-proyecto-socio-periodo-pago.fragment';
import { SolicitudProyectoPeriodoJustificacionesFragment } from './solicitud-proyecto-socio-formulario/solicitud-proyecto-periodo-justificaciones/solicitud-proyecto-periodo-justificaciones.fragment';
import { SolicitudProyectoPeriodoJustificacionService } from '@core/services/csp/solicitud-proyecto-periodo-justificacion.service';

@Injectable()
export class SolicitudProyectoSocioActionService extends ActionService {
  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datos-generales',
    PERIODOS_PAGOS: 'periodos-pagos',
    PERIODOS_JUSTIFICACION: 'periodos-justificacion',
  };

  private datosGenerales: SolicitudProyectoSocioDatosGeneralesFragment;
  private periodosPago: SolicitudProyectoSocioPeriodoPagoFragment;
  private periodoJustificaciones: SolicitudProyectoPeriodoJustificacionesFragment;

  private solicitudId: number;
  private solicitudProyectoSocio: ISolicitudProyectoSocio;
  private selectedSolicitudProyectoSocios: ISolicitudProyectoSocio[];

  constructor(
    logger: NGXLogger,
    solicitudService: SolicitudService,
    solicitudProyectoSocioService: SolicitudProyectoSocioService,
    solicitudProyectoPeriodoPagoService: SolicitudProyectoPeriodoPagoService,
    solicitudProyectoPeriodoJustificacionService: SolicitudProyectoPeriodoJustificacionService
  ) {
    super();
    this.solicitudProyectoSocio = {} as ISolicitudProyectoSocio;

    this.solicitudProyectoSocio = history.state.solicitudProyectoSocio;
    this.solicitudId = history.state.solicitudId;
    this.selectedSolicitudProyectoSocios = history.state.selectedSolicitudProyectoSocios;
    if (this.solicitudProyectoSocio?.id) {
      this.enableEdit();
    }

    this.datosGenerales = new SolicitudProyectoSocioDatosGeneralesFragment(
      logger, this.solicitudProyectoSocio?.id, solicitudProyectoSocioService, solicitudService, this);
    this.periodosPago = new SolicitudProyectoSocioPeriodoPagoFragment(logger, this.solicitudProyectoSocio?.id,
      solicitudProyectoSocioService, solicitudProyectoPeriodoPagoService);
    this.periodoJustificaciones = new SolicitudProyectoPeriodoJustificacionesFragment(logger, this.solicitudProyectoSocio?.id,
      solicitudProyectoSocioService, solicitudProyectoPeriodoJustificacionService, this);

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);
    this.addFragment(this.FRAGMENT.PERIODOS_PAGOS, this.periodosPago);
    this.addFragment(this.FRAGMENT.PERIODOS_JUSTIFICACION, this.periodoJustificaciones);
  }

  getSolicitudId(): number {
    return this.solicitudId;
  }

  getSolicitudProyectoSocio(): ISolicitudProyectoSocio {
    return this.datosGenerales.isInitialized() ? this.datosGenerales.getValue() : this.solicitudProyectoSocio;
  }

  getSelectedSolicitudProyectoSocios(): ISolicitudProyectoSocio[] {
    return this.selectedSolicitudProyectoSocios;
  }
}
