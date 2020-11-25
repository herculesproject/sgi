import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ITipoHito } from '@core/models/csp/tipos-configuracion';
import { environment } from '@env';
import { SgiRestFindOptions, SgiRestListResult, SgiRestService } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class TipoHitoService extends SgiRestService<number, ITipoHito> {
  private static readonly MAPPING = '/tipohitos';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      TipoHitoService.name,
      logger,
      `${environment.serviceServers.csp}${TipoHitoService.MAPPING}`,
      http
    );
  }

  /**
   * Muestra activos y no activos
   * @param options opciones de búsqueda.
   */
  findTodos(options?: SgiRestFindOptions): Observable<SgiRestListResult<ITipoHito>> {
    this.logger.debug(TipoHitoService.name, `${this.findTodos.name}(`, '-', 'START');
    return this.find<ITipoHito, ITipoHito>(`${this.endpointUrl}/todos`, options).pipe(
      tap(() => this.logger.debug(TipoHitoService.name, `${this.findTodos.name}()`, '-', 'END'))
    );
  }

  /**
   * Desactivar tipo hito
   * @param options opciones de búsqueda.
   */
  desactivar(id: number): Observable<void> {
    this.logger.debug(TipoHitoService.name, `${this.desactivar.name}(`, '-', 'start');
    return this.http.patch<void>(`${this.endpointUrl}/${id}/desactivar`, undefined).pipe(
      tap(() => this.logger.debug(TipoHitoService.name, `${this.desactivar.name}()`, '-', 'end'))
    );
  }

  /**
   * Reactivar tipo hito
   * @param options opciones de búsqueda.
   */
  reactivar(id: number): Observable<void> {
    this.logger.debug(TipoHitoService.name, `${this.reactivar.name}(`, '-', 'start');
    return this.http.patch<void>(`${this.endpointUrl}/${id}/reactivar`, undefined).pipe(
      tap(() => this.logger.debug(TipoHitoService.name, `${this.reactivar.name}()`, '-', 'end'))
    );
  }


}

