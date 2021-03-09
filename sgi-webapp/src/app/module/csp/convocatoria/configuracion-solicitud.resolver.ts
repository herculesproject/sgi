import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IConfiguracionSolicitud } from '@core/models/csp/configuracion-solicitud';
import { SgiResolverResolver } from '@core/resolver/sgi-resolver';
import { ConfiguracionSolicitudService } from '@core/services/csp/configuracion-solicitud.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';

const MSG_NOT_FOUND = marker('csp.convocatoria.editar.no-encontrado');

@Injectable()
export class ConfiguracionSolicitudResolver extends SgiResolverResolver<IConfiguracionSolicitud> {

  constructor(logger: NGXLogger, router: Router, snackBar: SnackBarService, private service: ConfiguracionSolicitudService) {
    super(logger, router, snackBar, MSG_NOT_FOUND);
  }

  protected resolveEntity(route: ActivatedRouteSnapshot): Observable<IConfiguracionSolicitud> {
    return this.service.findById(Number(route.paramMap.get('id')));
  }
}
