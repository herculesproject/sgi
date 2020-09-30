import { Injectable } from '@angular/core';
import { SgiRestService, SgiRestListResult, SgiRestFindOptions } from '@sgi/framework/http/';
import { NGXLogger } from 'ngx-logger';
import { HttpClient } from '@angular/common/http';
import { IPlan } from '@core/models/csp/plan';
import { environment } from '@env';
import { Observable, of } from 'rxjs';






const planes: IPlan[] = [
  {
    id: 1, nombre: 'Plan Nacional 2020 - 2023'
  },
  {
    id: 2, nombre: 'Plan propio'
  },


];

@Injectable({
  providedIn: 'root'
})
export class PlanService extends SgiRestService<number, IPlan> {

  private static readonly MAPPING = '/plan';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      PlanService.name,
      logger,
      `${environment.serviceServers.eti}${PlanService.MAPPING}`,
      http
    );
  }


  /**
   * Recupera listado mock de planes.
   * @param options opciones de búsqueda.
   * @returns listado de modelos de planes.
  */
  findPlanes(options?: SgiRestFindOptions): Observable<SgiRestListResult<IPlan>> {
    this.logger.debug(PlanService.name, `findPlanes(${options ? JSON.stringify(options) : ''})`, '-', 'START');

    return of({
      page: null,
      total: planes.length,
      items: planes
    } as SgiRestListResult<IPlan>);
  }
}
