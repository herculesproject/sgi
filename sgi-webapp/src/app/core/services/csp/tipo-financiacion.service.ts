import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ITipoFinanciacion } from '@core/models/csp/tipos-configuracion';
import { environment } from '@env';
import { SgiRestFindOptions, SgiRestListResult, SgiRestService } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class TipoFinanciacionService extends SgiRestService<number, ITipoFinanciacion> {
  private static readonly MAPPING = '/tipofinanciaciones';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      TipoFinanciacionService.name,
      logger,
      `${environment.serviceServers.csp}${TipoFinanciacionService.MAPPING}`,
      http
    );
  }

  /**
   * Muestra activos y no activos
   *
   * @param options opciones de búsqueda.
   */
  findTodos(options?: SgiRestFindOptions): Observable<SgiRestListResult<ITipoFinanciacion>> {
    this.logger.debug(TipoFinanciacionService.name, `${this.findTodos.name}(`, '-', 'START');
    return this.find<ITipoFinanciacion, ITipoFinanciacion>(`${this.endpointUrl}/todos`, options).pipe(
      tap(() => this.logger.debug(TipoFinanciacionService.name, `${this.findTodos.name}()`, '-', 'END'))
    );
  }
}
