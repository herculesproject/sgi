import { Injectable } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { IProyectoProyectoSge } from '@core/models/csp/proyecto-proyecto-sge';
import { ActionService } from '@core/services/action-service';
import { ProyectoProyectoSgeService } from '@core/services/csp/proyecto-proyecto-sge.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { ProyectoSgeService } from '@core/services/sge/proyecto-sge.service';
import { NGXLogger } from 'ngx-logger';
import { EJECUCION_ECONOMICA_DATA_KEY } from './ejecucion-economica-data.resolver';
import { ProyectosFragment } from './ejecucion-economica-formulario/proyectos/proyectos.fragment';
import { EJECUCION_ECONOMICA_ROUTE_PARAMS } from './ejecucion-economica-route-params';

export interface IEjecucionEconomicaData {
  readonly: boolean;
  proyectoProyectoSge: IProyectoProyectoSge;
}

@Injectable()
export class EjecucionEconomicaActionService extends ActionService {

  public readonly FRAGMENT = {
    PROYECTOS: 'proyectos',
  };

  private proyectos: ProyectosFragment;

  private readonly data: IEjecucionEconomicaData;

  constructor(
    logger: NGXLogger,
    route: ActivatedRoute,
    private proyectoProyectoSgeService: ProyectoProyectoSgeService,
    private proyectoService: ProyectoService,
    private proyectoSgeService: ProyectoSgeService
  ) {
    super();

    this.data = route.snapshot.data[EJECUCION_ECONOMICA_DATA_KEY];
    const id = Number(route.snapshot.paramMap.get(EJECUCION_ECONOMICA_ROUTE_PARAMS.ID));

    this.proyectos = new ProyectosFragment(id, this.data.proyectoProyectoSge.proyectoSge.id,
      proyectoProyectoSgeService, proyectoService, proyectoSgeService, this.data?.readonly);

    this.addFragment(this.FRAGMENT.PROYECTOS, this.proyectos);
  }

}
