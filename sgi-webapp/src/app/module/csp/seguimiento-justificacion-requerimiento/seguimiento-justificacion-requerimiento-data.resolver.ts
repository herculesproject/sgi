import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SgiResolverResolver } from '@core/resolver/sgi-resolver';
import { ProyectoProyectoSgeService } from '@core/services/csp/proyecto-proyecto-sge.service';
import { RequerimientoJustificacionService } from '@core/services/csp/requerimiento-justificacion/requerimiento-justificacion.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { NGXLogger } from 'ngx-logger';
import { Observable, of, throwError } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { SEGUIMIENTO_JUSTIFICACION_REQUERIMIENTO_ROUTE_PARAMS } from './seguimiento-justificacion-requerimiento-route-params';
import { IRequerimientoJustificacionData } from './seguimiento-justificacion-requerimiento.action.service';

const MSG_NOT_FOUND = marker('error.load');

export const REQUERIMIENTO_JUSTIFICACION_DATA_KEY = 'requerimientoJustificacionData';

@Injectable()
export class SeguimientoJustificacionRequerimientoDataResolver extends SgiResolverResolver<IRequerimientoJustificacionData> {

  constructor(
    logger: NGXLogger,
    router: Router,
    snackBar: SnackBarService,
    private service: RequerimientoJustificacionService,
    private authService: SgiAuthService,
    private readonly proyectoProyectoSgeService: ProyectoProyectoSgeService) {
    super(logger, router, snackBar, MSG_NOT_FOUND);
  }

  protected resolveEntity(route: ActivatedRouteSnapshot): Observable<IRequerimientoJustificacionData> {
    const repartoId = Number(route.paramMap.get(SEGUIMIENTO_JUSTIFICACION_REQUERIMIENTO_ROUTE_PARAMS.ID));

    return this.service.findById(repartoId).pipe(
      switchMap((requerimientoJustificacion) => {
        if (requerimientoJustificacion?.proyectoProyectoSge?.id) {
          return this.proyectoProyectoSgeService.findById(requerimientoJustificacion.proyectoProyectoSge.id)
            .pipe(
              map((proyectoProyectoSge) => {
                requerimientoJustificacion.proyectoProyectoSge = proyectoProyectoSge;
                return requerimientoJustificacion;
              })
            );
        } else {
          return of(requerimientoJustificacion);
        }
      }),
      map(requerimientoJustificacion => {
        if (!requerimientoJustificacion) {
          throwError('NOT_FOUND');
        }
        return {
          canEdit: this.authService.hasAuthority('CSP-SJUS-E'),
          requerimientoJustificacion
        };
      }));
  }
}
