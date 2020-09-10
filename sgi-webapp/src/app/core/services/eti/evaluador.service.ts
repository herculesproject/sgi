import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IEvaluacionSolicitante } from '@core/models/eti/dto/evaluacion-solicitante';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { IEvaluador } from '@core/models/eti/evaluador';
import { environment } from '@env';
import { SgiRestFindOptions, SgiRestListResult, SgiRestService } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class EvaluadorService extends SgiRestService<number, IEvaluador> {
  private static readonly MAPPING = '/evaluadores';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(EvaluadorService.name, logger, `${environment.serviceServers.eti}${EvaluadorService.MAPPING}`, http);
  }

  getEvaluaciones(userRefId: string, options?: SgiRestFindOptions): Observable<SgiRestListResult<IEvaluacionSolicitante>> {
    this.logger.debug(EvaluadorService.name,
      `getEvaluaciones(${userRefId}, ${options ? JSON.stringify(options) : options})`, '-', 'start');
    return this.find<IEvaluacion, IEvaluacionSolicitante>
      (`${this.endpointUrl}/${userRefId}/evaluaciones`, options).pipe(
        tap(() => this.logger.debug(EvaluadorService.name,
          `getEvaluaciones(${userRefId}, ${options ? JSON.stringify(options) : options})`, '-', 'end'))
      );
  }

  /**
   * Devuelve los seguimientos cuyo evaluador sea el usuario en sesión
   *
   * @param userRefId Identificador del usuario
   * @param options Opciones de filtrado y ordenación
   */
  getSeguimientos(userRefId: string, options?: SgiRestFindOptions):
    Observable<SgiRestListResult<IEvaluacion>> {
    this.logger.debug(EvaluadorService.name,
      `getSeguimientos(${userRefId}, ${options ? JSON.stringify(options) : options})`, '-', 'start');
    return this.find<IEvaluacion, IEvaluacion>
      (`${this.endpointUrl}/${userRefId}/evaluaciones-seguimiento`, options).pipe(
        tap(() => this.logger.debug(EvaluadorService.name,
          `getSeguimientos(${userRefId}, ${options ? JSON.stringify(options) : options})`, '-', 'end'))
      );
  }
}
