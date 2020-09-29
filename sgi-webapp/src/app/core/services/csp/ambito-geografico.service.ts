import { Injectable } from '@angular/core';
import { SgiRestService, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http/';
import { IAmbitoGeografico } from '@core/models/csp/ambito-geografico';
import { NGXLogger } from 'ngx-logger';
import { HttpClient } from '@angular/common/http';
import { environment } from '@env';
import { Observable, of } from 'rxjs';


const ambitosGeografico: IAmbitoGeografico[] = [
  {
    id: 1, nombre: 'Concesión directa'
  },
  {
    id: 2, nombre: 'Concurrencia competitiva'
  }

];

@Injectable({
  providedIn: 'root'
})
export class AmbitoGeograficoService extends SgiRestService<number, IAmbitoGeografico> {
  private static readonly MAPPING = '/ambito-geografico';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      AmbitoGeograficoService.name,
      logger,
      `${environment.serviceServers.eti}${AmbitoGeograficoService.MAPPING}`,
      http
    );
  }


  /**
   * Recupera listado mock de modelos de ámbito geográfico.
   * @param options opciones de búsqueda.
   * @returns listado de modelos de  ámbito geográfico.
   */
  findAmbitoGeografico(options?: SgiRestFindOptions): Observable<SgiRestListResult<IAmbitoGeografico>> {
    this.logger.debug(AmbitoGeograficoService.name, `findAmbitoGeografico(${options ? JSON.stringify(options) : ''})`, '-', 'START');

    return of({
      page: null,
      total: ambitosGeografico.length,
      items: ambitosGeografico
    } as SgiRestListResult<IAmbitoGeografico>);
  }


}
