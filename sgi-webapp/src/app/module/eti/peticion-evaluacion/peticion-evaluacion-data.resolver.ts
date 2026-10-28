import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IPeticionEvaluacion } from '@core/models/eti/peticion-evaluacion';
import { Module } from '@core/module';
import { SgiResolverResolver } from '@core/resolver/sgi-resolver';
import { ConfigService } from '@core/services/eti/config.service';
import { PeticionEvaluacionService } from '@core/services/eti/peticion-evaluacion.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { SgiAuthService } from '@herculesproject/framework/auth';
import { NGXLogger } from 'ngx-logger';
import { forkJoin, Observable, of, throwError } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { IPeticionEvaluacionData } from './peticion-evaluacion.action.service';

const MSG_NOT_FOUND = marker('error.load');

export const PETICION_EVALUACION_DATA_KEY = 'peticionEvaluacionData';

@Injectable()
export class PeticionEvaluacionDataResolver extends SgiResolverResolver<IPeticionEvaluacionData> {

  constructor(
    logger: NGXLogger,
    router: Router,
    snackBar: SnackBarService,
    private readonly service: PeticionEvaluacionService,
    private readonly authService: SgiAuthService,
    private readonly configService: ConfigService
  ) {
    super(logger, router, snackBar, MSG_NOT_FOUND);
  }

  protected resolveEntity(route: ActivatedRouteSnapshot): Observable<IPeticionEvaluacionData> {
    const id = Number(route.paramMap.get('id'));
    const isModuleInv = route.data.module === Module.INV;

    return this.checkAccess(id, isModuleInv).pipe(
      switchMap(() => forkJoin({
        peticionEvaluacion: id ? this.service.findById(id) : of(null as IPeticionEvaluacion),
        isUnidadesVinculacionEnabled: this.isUnidadesVinculacionReadable(id, isModuleInv)
          ? this.configService.isPeticionEvaluacionUnidadesVinculacionEnabled()
          : of(false),
        introduccionVisibility: this.configService.getPeticionEvaluacionIntroduccionVisibility(),
        ayudas: this.configService.getPeticionEvaluacionAyudas()
      }))
    );
  }

  /**
   * Comprueba el acceso a la petición de evaluación además de las autoridades que exige la ruta.
   */
  private checkAccess(id: number, isModuleInv: boolean): Observable<void> {
    // Alta: todavía no hay petición de evaluación
    if (!id) {
      return of(void 0);
    }

    // Gestión: basta con las autoridades de la ruta
    if (!isModuleInv) {
      return of(void 0);
    }

    // Investigación: además tiene que ser responsable o creador de la petición de evaluación
    return this.service.isResponsableOrCreador(id).pipe(
      switchMap(isResponsableOrCreador => isResponsableOrCreador ? of(void 0) : throwError('NOT_FOUND'))
    );
  }

  /**
   * La configuración de unidades de vinculación solo se consulta si el apartado puede mostrarse: en el módulo de
   * gestión, en la edición y con permiso para consultar las unidades de vinculación.
   */
  private isUnidadesVinculacionReadable(id: number, isModuleInv: boolean): boolean {
    return !isModuleInv && !!id && this.authService.hasAnyAuthorityForAnyUO(['ETI-PEV-V', 'ETI-PEV-E']);
  }

}
