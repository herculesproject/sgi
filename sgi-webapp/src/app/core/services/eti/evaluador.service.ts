
import { Injectable } from '@angular/core';
import { IEvaluacionSolicitante } from '@core/models/eti/evaluacion-solicitante';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { IEvaluador } from '@core/models/eti/evaluador';
import { environment } from '@env';
import { SgiRestFindOptions, SgiRestListResult, SgiRestService } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class EvaluadorService extends SgiRestService<number, IEvaluador>{

  private static readonly MAPPING = '/evaluadores';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(EvaluadorService.name, logger, `${environment.serviceServers.eti}${EvaluadorService.MAPPING}`, http);
  }

  /**
   * Devuelve las evaluaciones cuyo evaluador sea el usuario en sesión
   *
   * @param options Opciones de filtrado y ordenación
   */
  getEvaluaciones(options?: SgiRestFindOptions): Observable<SgiRestListResult<IEvaluacionSolicitante>> {
    this.logger.debug(EvaluadorService.name,
      `getEvaluaciones(${options ? JSON.stringify(options) : options})`, '-', 'start');
    return this.find<IEvaluacion, IEvaluacionSolicitante>
      (`${this.endpointUrl}/evaluaciones`, options).pipe(
        tap(() => this.logger.debug(EvaluadorService.name,
          `getEvaluaciones(${options ? JSON.stringify(options) : options})`, '-', 'end'))
      );
  }

  /**
   * Devuelve los seguimientos cuyo evaluador sea el usuario en sesión
   *
   * @param options Opciones de filtrado y ordenación
   */
  getSeguimientos(options?: SgiRestFindOptions):
    Observable<SgiRestListResult<IEvaluacionSolicitante>> {
    this.logger.debug(EvaluadorService.name,
      `getSeguimientos( ${options ? JSON.stringify(options) : options})`, '-', 'start');
    return this.find<IEvaluacion, IEvaluacionSolicitante>(`${this.endpointUrl}/evaluaciones-seguimiento`, options)
      .pipe(
        tap(() => this.logger.debug(EvaluadorService.name,
          `getSeguimientos(${options ? JSON.stringify(options) : options})`, '-', 'end'))
      );
  }

  /**
   * Devuelve todos las memorias asignables a la convocatoria
   *
   * @param idComite id comite.
   * @param idMemoria id memoria.
   * @param options opciones de busqueda.
   * @return las memorias asignables a la convocatoria.
   */
  findAllMemoriasAsignablesConvocatoria(idComite: number, idMemoria: number, options?: SgiRestFindOptions):
    Observable<SgiRestListResult<IEvaluador>> {
    this.logger.debug(EvaluadorService.name, `findAllMemoriasAsignablesConvocatoria(${idComite},${idMemoria})`, '-', 'START');
    return this.find<IEvaluador, IEvaluador>(`${this.endpointUrl}/comite/${idComite}/sinconflictointereses/${idMemoria}`, options).pipe(
      tap(() => this.logger.debug(EvaluadorService.name, `findAllMemoriasAsignablesConvocatoria(${idComite},${idMemoria})`, '-', 'END'))
    );
  }

}
