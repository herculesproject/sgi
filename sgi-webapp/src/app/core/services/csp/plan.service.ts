import { Injectable } from '@angular/core';
import { SgiRestService, SgiRestListResult, SgiRestFindOptions } from '@sgi/framework/http/';
import { NGXLogger } from 'ngx-logger';
import { HttpClient } from '@angular/common/http';
import { environment } from '@env';
import { Observable, of } from 'rxjs';
import { tap } from 'rxjs/operators';
import { IPlan } from '@core/models/csp/tipos-configuracion';

@Injectable({
  providedIn: 'root'
})
export class PlanService extends SgiRestService<number, IPlan> {

  private static readonly MAPPING = '/planes';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      PlanService.name,
      logger,
      `${environment.serviceServers.csp}${PlanService.MAPPING}`,
      http
    );
  }

  /**
   * Muestra activos y no activos
   * @param options opciones de búsqueda.
   */
  findTodos(options?: SgiRestFindOptions): Observable<SgiRestListResult<IPlan>> {
    this.logger.debug(PlanService.name, `${this.findTodos.name}(`, '-', 'START');
    return this.find<IPlan, IPlan>(`${this.endpointUrl}/todos`, options).pipe(
      tap(() => this.logger.debug(PlanService.name, `${this.findTodos.name}()`, '-', 'END'))
    );
  }

}
