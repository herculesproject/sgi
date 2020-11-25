import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ITipoEnlace } from '@core/models/csp/tipos-configuracion';
import { environment } from '@env';
import { SgiRestFindOptions, SgiRestListResult, SgiRestService } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class TipoEnlaceService extends SgiRestService<number, ITipoEnlace> {
  private static readonly MAPPING = '/tipoenlaces';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      TipoEnlaceService.name,
      logger,
      `${environment.serviceServers.csp}${TipoEnlaceService.MAPPING}`,
      http
    );
  }

  /**
   * Muestra activos y no activos
   * @param options opciones de búsqueda.
   */
  findTodos(options?: SgiRestFindOptions): Observable<SgiRestListResult<ITipoEnlace>> {
    this.logger.debug(TipoEnlaceService.name, `${this.findTodos.name}(`, '-', 'START');
    return this.find<ITipoEnlace, ITipoEnlace>(`${this.endpointUrl}/todos`, options).pipe(
      tap(() => this.logger.debug(TipoEnlaceService.name, `${this.findTodos.name}()`, '-', 'END'))
    );
  }

  /**
   * Desactivar tipo enlace
   * @param options opciones de búsqueda.
   */
  desactivar(id: number): Observable<void> {
    this.logger.debug(TipoEnlaceService.name, `${this.desactivar.name}(`, '-', 'start');
    return this.http.patch<void>(`${this.endpointUrl}/${id}/desactivar`, undefined).pipe(
      tap(() => this.logger.debug(TipoEnlaceService.name, `${this.desactivar.name}()`, '-', 'end'))
    );
  }

  /**
   * Reactivar tipo enlace
   * @param options opciones de búsqueda.
   */
  reactivar(id: number): Observable<void> {
    this.logger.debug(TipoEnlaceService.name, `${this.reactivar.name}(`, '-', 'start');
    return this.http.patch<void>(`${this.endpointUrl}/${id}/reactivar`, undefined).pipe(
      tap(() => this.logger.debug(TipoEnlaceService.name, `${this.reactivar.name}()`, '-', 'end'))
    );
  }

}
