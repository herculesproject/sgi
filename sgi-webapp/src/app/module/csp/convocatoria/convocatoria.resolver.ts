import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SgiResolverResolver } from '@core/resolver/sgi-resolver';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

const MSG_NOT_FOUND = marker('csp.convocatoria.editar.no-encontrado');

@Injectable()
export class ConvocatoriaResolver extends SgiResolverResolver<number> {

  constructor(logger: NGXLogger, router: Router, snackBar: SnackBarService, private service: ConvocatoriaService) {
    super(logger, router, snackBar, MSG_NOT_FOUND);
  }

  protected resolveEntity(route: ActivatedRouteSnapshot): Observable<number> {
    const id = Number(route.paramMap.get('id'));
    return this.service.exists(id).pipe(map(() => id));
  }
}
