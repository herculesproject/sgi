import { Injectable } from '@angular/core';
import { ActionService } from '@core/services/action-service';
import { NGXLogger } from 'ngx-logger';
import { ProyectoSocioDatosGeneralesFragment } from './proyecto-socio-formulario/proyecto-socio-datos-generales/proyecto-socio-datos-generales.fragment';
import { ProyectoSocioService } from '@core/services/csp/proyecto-socio.service';
import { EmpresaEconomicaService } from '@core/services/sgp/empresa-economica.service';
import { IProyectoSocio } from '@core/models/csp/proyecto-socio';
import { ProyectoSocioEquipoFragment } from './proyecto-socio-formulario/proyecto-socio-equipo/proyecto-socio-equipo.fragment';
import { ProyectoSocioEquipoService } from '@core/services/csp/proyecto-socio-equipo.service';
import { PersonaFisicaService } from '@core/services/sgp/persona-fisica.service';
import { ProyectoSocioPeriodoPagoFragment } from './proyecto-socio-formulario/proyecto-socio-periodo-pago/proyecto-socio-periodo-pago.fragment';
import { ProyectoSocioPeriodoPagoService } from '@core/services/csp/proyecto-socio-periodo-pago.service';
import { ProyectoSocioPeriodoJustificacionFragment } from './proyecto-socio-formulario/proyecto-socio-periodo-justificacion/proyecto-socio-periodo-justificacion.fragment';
import { ProyectoSocioPeriodoJustificacionService } from '@core/services/csp/proyecto-socio-periodo-justificacion.service';

@Injectable()
export class ProyectoSocioActionService extends ActionService {

  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datosGenerales',
    EQUIPO: 'equipo',
    PERIODO_PAGO: 'periodo-pago',
    PERIODO_JUSTIFICACION: 'periodo-justificacion'
  };

  private datosGenerales: ProyectoSocioDatosGeneralesFragment;
  private equipo: ProyectoSocioEquipoFragment;
  private periodoPago: ProyectoSocioPeriodoPagoFragment;
  private periodosJustificacion: ProyectoSocioPeriodoJustificacionFragment;

  private urlProyecto: string;
  private proyectoId: number;
  private proyectoSocio: IProyectoSocio;
  private selectedProyectoSocios: IProyectoSocio[];

  constructor(
    logger: NGXLogger,
    empresaEconomicaService: EmpresaEconomicaService,
    proyectoSocioService: ProyectoSocioService,
    proyectoEquipoSocioService: ProyectoSocioEquipoService,
    personaFisicaService: PersonaFisicaService,
    proyectoSocioPeriodoPagoService: ProyectoSocioPeriodoPagoService,
    proyectoSocioPeriodoJustificacionService: ProyectoSocioPeriodoJustificacionService
  ) {
    super();

    this.urlProyecto = history.state.urlProyecto;
    this.proyectoSocio = history.state.proyectoSocio;
    this.proyectoId = history.state.proyectoId;
    this.selectedProyectoSocios = history?.state?.selectedProyectoSocios;

    if (this.proyectoSocio?.id) {
      this.enableEdit();
      this.periodosJustificacion = new ProyectoSocioPeriodoJustificacionFragment(logger, this.proyectoSocio?.id,
        proyectoSocioService, proyectoSocioPeriodoJustificacionService);
      this.addFragment(this.FRAGMENT.PERIODO_JUSTIFICACION, this.periodosJustificacion);
    }

    this.datosGenerales = new ProyectoSocioDatosGeneralesFragment(logger, this.proyectoSocio?.id, this.proyectoId,
      proyectoSocioService, empresaEconomicaService);
    this.equipo = new ProyectoSocioEquipoFragment(logger, this.proyectoSocio?.id, proyectoSocioService,
      proyectoEquipoSocioService, personaFisicaService);
    this.periodoPago = new ProyectoSocioPeriodoPagoFragment(logger, this.proyectoSocio?.id, proyectoSocioService,
      proyectoSocioPeriodoPagoService);

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);
    this.addFragment(this.FRAGMENT.EQUIPO, this.equipo);
    this.addFragment(this.FRAGMENT.PERIODO_PAGO, this.periodoPago);
  }

  getUrlProyecto(): string {
    return this.urlProyecto;
  }

  getProyectoId(): number {
    return this.proyectoId;
  }

  getProyectoSocio(): IProyectoSocio {
    return this.proyectoSocio;
  }

  getSelectedProyectoSocios(): IProyectoSocio[] {
    return this.selectedProyectoSocios;
  }
}
