import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { ISolicitud } from '@core/models/csp/solicitud';
import { SgiResolverResolver } from '@core/resolver/sgi-resolver';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';

const MSG_NOT_FOUND = marker('csp.solicitud.editar.no-encontrado');

@Injectable()
export class SolicitudResolver extends SgiResolverResolver<ISolicitud> {

  constructor(logger: NGXLogger, router: Router, snackBar: SnackBarService, private service: SolicitudService) {
    super(logger, router, snackBar, MSG_NOT_FOUND);
  }

  protected resolveEntity(route: ActivatedRouteSnapshot): Observable<ISolicitud> {
    return this.service.findById(Number(route.paramMap.get('id')));
  }
}
