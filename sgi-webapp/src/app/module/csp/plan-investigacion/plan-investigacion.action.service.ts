import { Injectable } from '@angular/core';

import { ActionService } from '@core/services/action-service';
import { ActivatedRoute } from '@angular/router';

import { NGXLogger } from 'ngx-logger';
import { PlanService } from '@core/services/csp/plan.service';
import { IPlan } from '@core/models/csp/tipos-configuracion';


@Injectable()
export class PlanInvestigacionActionService extends ActionService {
  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datos-generales',
  };

  private plan: IPlan;

  constructor(
    private readonly logger: NGXLogger,
    route: ActivatedRoute,
    planService: PlanService,
  ) {
    super();
    this.logger.debug(PlanInvestigacionActionService.name, 'constructor()', 'start');
    this.plan = {} as IPlan;
    if (route.snapshot.data.plan) {
      this.plan = route.snapshot.data.plan;
      this.enableEdit();
    }

    this.logger.debug(PlanInvestigacionActionService.name, 'constructor()', 'start');
    this.logger.debug(PlanInvestigacionActionService.name, 'constructor()', 'end');
  }
}
