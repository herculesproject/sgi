import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ITipoOrigenFuenteFinanciacion } from '@core/models/csp/tipo-origen-fuente-financiacion';
import { environment } from '@env';
import { SgiRestFindOptions, SgiRestListResult, SgiRestService } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TipoOrigenFuenteFinanciacionService extends SgiRestService<number, ITipoOrigenFuenteFinanciacion> {
  private static readonly MAPPING = '/tipoorigenfuentefinanciaciones';

  constructor(protected readonly logger: NGXLogger, protected http: HttpClient) {
    super(
      TipoOrigenFuenteFinanciacionService.name,
      logger,
      `${environment.serviceServers.csp}${TipoOrigenFuenteFinanciacionService.MAPPING}`,
      http
    );
  }

  /**
   * Muestra activos y no activos
   *
   * @param options opciones de búsqueda.
   */
  findTodos(options?: SgiRestFindOptions): Observable<SgiRestListResult<ITipoOrigenFuenteFinanciacion>> {
    return this.find<ITipoOrigenFuenteFinanciacion, ITipoOrigenFuenteFinanciacion>(`${this.endpointUrl}/todos`, options);
  }
}
