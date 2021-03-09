import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IProyecto } from '@core/models/csp/proyecto';
import { SgiResolverResolver } from '@core/resolver/sgi-resolver';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';

const MSG_NOT_FOUND = marker('csp.proyecto.editar.no-encontrado');

@Injectable()
export class ProyectoResolver extends SgiResolverResolver<IProyecto> {

  constructor(logger: NGXLogger, router: Router, snackBar: SnackBarService, private service: ProyectoService) {
    super(logger, router, snackBar, MSG_NOT_FOUND);
  }

  protected resolveEntity(route: ActivatedRouteSnapshot): Observable<IProyecto> {
    return this.service.findById(Number(route.paramMap.get('id')));
  }
}
