import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IGrupo } from '@core/models/csp/grupo';
import { Module } from '@core/module';
import { SgiResolverResolver } from '@core/resolver/sgi-resolver';
import { ConfigService } from '@core/services/csp/configuracion/config.service';
import { GrupoService } from '@core/services/csp/grupo/grupo.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { SgiAuthService } from '@herculesproject/framework/auth';
import { NGXLogger } from 'ngx-logger';
import { forkJoin, Observable, of, throwError } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { GRUPO_ROUTE_PARAMS } from './grupo-route-params';
import { IGrupoData } from './grupo.action.service';

const MSG_NOT_FOUND = marker('error.load');

export const GRUPO_DATA_KEY = 'grupoData';

@Injectable()
export class GrupoDataResolver extends SgiResolverResolver<IGrupoData> {

  constructor(
    logger: NGXLogger,
    router: Router,
    snackBar: SnackBarService,
    private service: GrupoService,
    private authService: SgiAuthService,
    private configService: ConfigService
  ) {
    super(logger, router, snackBar, MSG_NOT_FOUND);
  }

  protected resolveEntity(route: ActivatedRouteSnapshot): Observable<IGrupoData> {
    const grupoId = Number(route.paramMap.get(GRUPO_ROUTE_PARAMS.ID));

    return this.service.findById(grupoId).pipe(
      switchMap(value => {
        if (!value) {
          return throwError('NOT_FOUND');
        }

        const isInvestigador = this.hasViewAuthorityInv() && route.data.module === Module.INV;
        const isUO = this.hasViewAuthorityUO() && route.data.module === Module.CSP;

        if (!isInvestigador && !isUO) {
          return throwError('NOT_FOUND');
        }

        return forkJoin({
          isReadOnly: this.isReadOnly$(isInvestigador, grupoId),
          isEjecucionEconomicaGruposEnabled: this.configService.isEjecucionEconomicaGruposEnabled(),
          isGrupoUnidadesVinculacionEnabled: this.configService.isGrupoUnidadesVinculacionEnabled(),
        }).pipe(
          map(({ isReadOnly, isEjecucionEconomicaGruposEnabled, isGrupoUnidadesVinculacionEnabled }) => {
            return {
              grupo: { id: grupoId } as IGrupo,
              isGrupoEspecialInvestigacion: value.especialInvestigacion,
              isInvestigador,
              readonly: isReadOnly,
              isEjecucionEconomicaGruposEnabled,
              isGrupoUnidadesVinculacionEnabled,
            };
          })
        );
      })
    );
  }

  private isReadOnly$(isInvestigador: boolean, grupoId: number): Observable<boolean> {
    if (isInvestigador) {
      return of(true);
    }

    if (grupoId) {
      return this.service.modificable(grupoId).pipe(
        map(modificable => !modificable)
      );
    }

    return of(false);
  }

  private hasViewAuthorityInv(): boolean {
    return this.authService.hasAuthority('CSP-GIN-INV-VR');
  }

  private hasViewAuthorityUO(): boolean {
    return this.authService.hasAnyAuthorityForAnyUO(
      [
        'CSP-GIN-E',
        'CSP-GIN-V'
      ]
    );
  }

}
