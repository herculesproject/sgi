import { Injectable } from '@angular/core';
import { SgiRestService, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http/';
import { NGXLogger } from 'ngx-logger';
import { HttpClient } from '@angular/common/http';
import { environment } from '@env';
import { of, Observable } from 'rxjs';
import { IRegimenConcurrencia } from '@core/models/csp/regimen-concurrencia';


const regimenesConcurrencia: IRegimenConcurrencia[] = [
  {
    id: 1, nombre: 'Concurrencia competitiva'
  },
  {
    id: 2, nombre: 'Contratación RRHH'
  },


];

@Injectable({
  providedIn: 'root'
})
export class RegimenConcurrenciaService extends SgiRestService<number, IRegimenConcurrencia> {
  private static readonly MAPPING = '/regimen-ocurrencia';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      RegimenConcurrenciaService.name,
      logger,
      `${environment.serviceServers.csp}${RegimenConcurrenciaService.MAPPING}`,
      http
    );
  }


  /**
   * Recupera listado mock de régimenes de ocurrencia.
   * @param options opciones de búsqueda.
   * @returns listado de modelos de régimenes de ocurrencia.
   */
  findRegimenConcurrencia(options?: SgiRestFindOptions): Observable<SgiRestListResult<IRegimenConcurrencia>> {
    this.logger.debug(RegimenConcurrenciaService.name, `findRegimenConcurrencia(${options ? JSON.stringify(options) : ''})`, '-', 'START');

    return of({
      page: null,
      total: regimenesConcurrencia.length,
      items: regimenesConcurrencia
    } as SgiRestListResult<IRegimenConcurrencia>);
  }

}
