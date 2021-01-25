import { Injectable } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ActionService } from '@core/services/action-service';
import { NGXLogger } from 'ngx-logger';
import { ProyectoSocioDatosGeneralesFragment } from './proyecto-socio-formulario/proyecto-socio-datos-generales/proyecto-socio-datos-generales.fragment';
import { ProyectoSocioService } from '@core/services/csp/proyecto-socio.service';
import { EmpresaEconomicaService } from '@core/services/sgp/empresa-economica.service';
import { IProyectoSocio } from '@core/models/csp/proyecto-socio';
import { ProyectoSocioEquipoFragment } from './proyecto-socio-formulario/proyecto-socio-equipo/proyecto-socio-equipo.fragment';
import { ProyectoSocioEquipoService } from '@core/services/csp/proyecto-socio-equipo.service';
import { PersonaFisicaService } from '@core/services/sgp/persona-fisica.service';

@Injectable()
export class ProyectoSocioActionService extends ActionService {

  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datosGenerales',
    EQUIPO: 'equipo'
  };

  private datosGenerales: ProyectoSocioDatosGeneralesFragment;
  private equipo: ProyectoSocioEquipoFragment;

  private proyectoId: number;
  private proyectoSocio: IProyectoSocio;
  private selectedProyectoSocios: IProyectoSocio[];

  constructor(
    logger: NGXLogger,
    empresaEconomicaService: EmpresaEconomicaService,
    proyectoSocioService: ProyectoSocioService,
    proyectoEquipoSocioService: ProyectoSocioEquipoService,
    personaFisicaService: PersonaFisicaService
  ) {
    super();

    this.proyectoSocio = history.state.proyectoSocio;
    this.proyectoId = history.state.proyectoId;
    this.selectedProyectoSocios = history.state.selectedProyectoSocios;

    if (this.proyectoSocio?.id) {
      this.enableEdit();
    }

    this.datosGenerales = new ProyectoSocioDatosGeneralesFragment(logger, this.proyectoSocio?.id, this.proyectoId,
      proyectoSocioService, empresaEconomicaService, this);
    this.equipo = new ProyectoSocioEquipoFragment(logger, this.proyectoSocio?.id, proyectoSocioService,
      proyectoEquipoSocioService, personaFisicaService);

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);
    this.addFragment(this.FRAGMENT.EQUIPO, this.equipo);
  }

  getSelectedProyectoSocios(): IProyectoSocio[] {
    return this.selectedProyectoSocios;
  }
}
