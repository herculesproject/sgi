import { Injectable } from '@angular/core';

import { ActionService } from '@core/services/action-service';
import { ActivatedRoute } from '@angular/router';

import { NGXLogger } from 'ngx-logger';
import { PlanService } from '@core/services/csp/plan.service';
import { IPlan } from '@core/models/csp/tipos-configuracion';
import { PlanInvestigacionDatosGeneralesFragment } from './plan-investigacion-formulario/plan-investigacion-datos-generales/plan-investigacion-datos-generales.fragment';


@Injectable()
export class PlanInvestigacionActionService extends ActionService {
  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datos-generales',
    PROGRAMAS: 'programas'
  };

  private plan: IPlan;
  private datosGenerales: PlanInvestigacionDatosGeneralesFragment;

  constructor(
    private readonly logger: NGXLogger,
    route: ActivatedRoute,
    planService: PlanService
  ) {
    super();
    this.logger.debug(PlanInvestigacionActionService.name, 'constructor()', 'start');
    this.plan = {} as IPlan;
    if (route.snapshot.data.plan) {
      this.plan = route.snapshot.data.plan;
      this.enableEdit();
    }

    this.datosGenerales = new PlanInvestigacionDatosGeneralesFragment(logger, this.plan?.id,
      planService, this);

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);

    this.logger.debug(PlanInvestigacionActionService.name, 'constructor()', 'start');
    this.logger.debug(PlanInvestigacionActionService.name, 'constructor()', 'end');
  }
}
