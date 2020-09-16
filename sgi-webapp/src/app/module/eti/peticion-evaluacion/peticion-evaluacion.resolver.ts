import { Injectable } from '@angular/core';

import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { SnackBarService } from '@core/services/snack-bar.service';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';

import { SgiResolverResolver } from '@core/resolver/sgi-resolver';
import { PeticionEvaluacion } from '@core/models/eti/peticion-evaluacion';
import { PeticionEvaluacionService } from '@core/services/eti/peticion-evaluacion.service';


const MSG_NOT_FOUND = marker('eti.peticionEvaluacion.editar.no-encontrado');

@Injectable()
export class PeticionEvaluacionResolver extends SgiResolverResolver<PeticionEvaluacion> {

  constructor(router: Router, snackBar: SnackBarService, private service: PeticionEvaluacionService) {
    super(router, snackBar, MSG_NOT_FOUND);
  }

  protected resolveEntity(route: ActivatedRouteSnapshot): Observable<PeticionEvaluacion> {
    return this.service.findById(Number(route.paramMap.get('id')));
  }
}
