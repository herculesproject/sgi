import { Injectable } from '@angular/core';
import { ActionService } from '@core/services/action-service';
import { SolicitudProyectoSocioDatosGeneralesFragment } from './solicitud-proyecto-socio-formulario/solicitud-proyecto-socio-datos-generales/solicitud-proyecto-socio-datos-generales.fragment';
import { ISolicitudProyectoSocio } from '@core/models/csp/solicitud-proyecto-socio';
import { NGXLogger } from 'ngx-logger';
import { SolicitudProyectoSocioService } from '@core/services/csp/solicitud-proyecto-socio.service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { SolicitudProyectoPeriodoPagoService } from '@core/services/csp/solicitud-proyecto-periodo-pago.service';
import { SolicitudProyectoSocioPeriodoPagoFragment } from './solicitud-proyecto-socio-formulario/solicitud-proyecto-socio-periodo-pago/solicitud-proyecto-socio-periodo-pago.fragment';
import { Observable, throwError } from 'rxjs';
import { tap, switchMap } from 'rxjs/operators';

@Injectable()
export class SolicitudProyectoSocioActionService extends ActionService {
  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datos-generales',
    PERIODOS_PAGOS: 'periodos-pagos'
  };

  solicitudId: number;

  datosGenerales: SolicitudProyectoSocioDatosGeneralesFragment;
  periodosPago: SolicitudProyectoSocioPeriodoPagoFragment;

  solicitudProyectoSocio: ISolicitudProyectoSocio;
  selectedSolicitudProyectoSocios: ISolicitudProyectoSocio[];

  constructor(
    logger: NGXLogger,
    solicitudService: SolicitudService,
    solicitudProyectoSocioService: SolicitudProyectoSocioService,
    solicitudProyectoPeriodoPagoService: SolicitudProyectoPeriodoPagoService
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

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);
    this.addFragment(this.FRAGMENT.PERIODOS_PAGOS, this.periodosPago);
  }
}
