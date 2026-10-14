import { Injectable } from '@angular/core';
import { Resolve } from '@angular/router';
import { ConfigService } from '@core/services/eti/config.service';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

export const PETICION_EVALUACION_UNIDADES_VINCULACION_ENABLED_KEY = 'isPeticionEvaluacionUnidadesVinculacionEnabled';

/**
 * Resuelve si está habilitado el apartado de unidades de vinculación de la petición de evaluación.
 * Si no se puede recuperar la configuración se considera deshabilitado.
 */
@Injectable()
export class PeticionEvaluacionUnidadesVinculacionEnabledResolver implements Resolve<boolean> {

  constructor(
    private readonly logger: NGXLogger,
    private readonly configService: ConfigService
  ) { }

  resolve(): Observable<boolean> {
    return this.configService.isPeticionEvaluacionUnidadesVinculacionEnabled().pipe(
      catchError(error => {
        this.logger.error(error);
        return of(false);
      })
    );
  }

}
