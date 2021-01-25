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

@Injectable()
export class ProyectoSocioActionService extends ActionService {

  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datosGenerales',
    EQUIPO: 'equipo',
    PERIODO_PAGO: 'periodo-pago'
  };

  private datosGenerales: ProyectoSocioDatosGeneralesFragment;
  private equipo: ProyectoSocioEquipoFragment;
  private periodoPago: ProyectoSocioPeriodoPagoFragment;

  private proyectoId: number;
  private proyectoSocio: IProyectoSocio;
  private selectedProyectoSocios: IProyectoSocio[];

  constructor(
    logger: NGXLogger,
    empresaEconomicaService: EmpresaEconomicaService,
    proyectoSocioService: ProyectoSocioService,
    proyectoEquipoSocioService: ProyectoSocioEquipoService,
    personaFisicaService: PersonaFisicaService,
    proyectoSocioPeriodoPagoService: ProyectoSocioPeriodoPagoService
  ) {
    super();

    this.proyectoSocio = history.state.proyectoSocio;
    this.proyectoId = history.state.proyectoId;
    this.selectedProyectoSocios = history?.state?.selectedProyectoSocios;

    if (this.proyectoSocio?.id) {
      this.enableEdit();
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

  getSelectedProyectoSocios(): IProyectoSocio[] {
    return this.selectedProyectoSocios;
  }
}
