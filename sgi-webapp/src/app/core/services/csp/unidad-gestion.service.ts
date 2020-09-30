import { Injectable } from '@angular/core';
import { SgiRestService, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http/';
import { IUnidadGestion } from '@core/models/csp/unidad-gestion';
import { NGXLogger } from 'ngx-logger';
import { HttpClient } from '@angular/common/http';
import { environment } from '@env';
import { of, Observable } from 'rxjs';


const unidadesGestion: IUnidadGestion[] = [
  {
    id: 1, nombre: 'OTRI'
  },
  {
    id: 2, nombre: 'OPE'
  },
  {
    id: 3, nombre: 'UGI'
  }

];

@Injectable({
  providedIn: 'root'
})
export class UnidadGestionService extends SgiRestService<number, IUnidadGestion> {
  private static readonly MAPPING = '/unidad-gestion';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      UnidadGestionService.name,
      logger,
      `${environment.serviceServers.csp}${UnidadGestionService.MAPPING}`,
      http
    );
  }


  /**
   * Recupera listado mock de unidades de gestión.
   * @param options opciones de búsqueda.
   * @returns listado de modelos de unidades de gestión.
   */
  findUnidadesGestion(options?: SgiRestFindOptions): Observable<SgiRestListResult<IUnidadGestion>> {
    this.logger.debug(UnidadGestionService.name, `findUnidadesGestion(${options ? JSON.stringify(options) : ''})`, '-', 'START');

    return of({
      page: null,
      total: unidadesGestion.length,
      items: unidadesGestion
    } as SgiRestListResult<IUnidadGestion>);
  }

}
