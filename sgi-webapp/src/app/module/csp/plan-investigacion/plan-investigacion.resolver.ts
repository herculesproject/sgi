import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { SnackBarService } from '@core/services/snack-bar.service';
import { SgiResolverResolver } from '@core/resolver/sgi-resolver';
import { IPlan } from '@core/models/csp/tipos-configuracion';
import { PlanService } from '@core/services/csp/plan.service';

const MSG_NOT_FOUND = marker('csp.plan.investigacion.editar.no-encontrado');

@Injectable()
export class PlanInvestigacionResolver extends SgiResolverResolver<IPlan> {

  constructor(router: Router, snackBar: SnackBarService, private service: PlanService) {
    super(router, snackBar, MSG_NOT_FOUND);
  }

  protected resolveEntity(route: ActivatedRouteSnapshot): Observable<IPlan> {
    return this.service.findById(Number(route.paramMap.get('id')));
  }
}
