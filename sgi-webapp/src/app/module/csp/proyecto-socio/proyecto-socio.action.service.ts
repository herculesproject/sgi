import { Injectable } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ActionService } from '@core/services/action-service';
import { NGXLogger } from 'ngx-logger';
import { ProyectoSocioDatosGeneralesFragment } from './proyecto-socio-formulario/proyecto-socio-datos-generales/proyecto-socio-datos-generales.fragment';
import { ProyectoSocioService } from '@core/services/csp/proyecto-socio.service';
import { EmpresaEconomicaService } from '@core/services/sgp/empresa-economica.service';
import { IProyectoSocio } from '@core/models/csp/proyecto-socio';



@Injectable()
export class ProyectoSocioActionService extends ActionService {

  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datosGenerales'
  };

  private datosGenerales: ProyectoSocioDatosGeneralesFragment;

  private proyectoId: number;
  private proyectoSocio: IProyectoSocio;
  selectedProyectoSocios: IProyectoSocio[];

  constructor(
    private logger: NGXLogger,
    route: ActivatedRoute,
    empresaEconomicaService: EmpresaEconomicaService,
    proyectoSocioService: ProyectoSocioService
  ) {
    super();

    this.logger = logger;

    this.proyectoSocio = history.state.proyectoSocio;
    this.proyectoId = history.state.proyectoId;
    this.selectedProyectoSocios = history.state.selectedProyectoSocios;

    if (this.proyectoSocio?.id) {
      this.enableEdit();
    }

    this.datosGenerales = new ProyectoSocioDatosGeneralesFragment(logger, this.proyectoSocio?.id, this.proyectoId, proyectoSocioService, empresaEconomicaService, this);


    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);
  }

}
