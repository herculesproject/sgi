import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ITipoFinalidad } from '@core/models/csp/tipos-configuracion';
import { environment } from '@env';
import { SgiRestFindOptions, SgiRestListResult, SgiRestService } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class TipoFinalidadService extends SgiRestService<number, ITipoFinalidad> {
  private static readonly MAPPING = '/tipofinalidades';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      TipoFinalidadService.name,
      logger,
      `${environment.serviceServers.csp}${TipoFinalidadService.MAPPING}`,
      http
    );
  }

  /**
   * Muestra activos y no activos
   *
   * @param options opciones de búsqueda.
   */
  findTodos(options?: SgiRestFindOptions): Observable<SgiRestListResult<ITipoFinalidad>> {
    this.logger.debug(TipoFinalidadService.name, `${this.findTodos.name}(`, '-', 'START');
    return this.find<ITipoFinalidad, ITipoFinalidad>(`${this.endpointUrl}/todos`, options).pipe(
      tap(() => this.logger.debug(TipoFinalidadService.name, `${this.findTodos.name}()`, '-', 'END'))
    );
  }

  /**
   * Desactivar tipo finalidad
   * @param options opciones de búsqueda.
   */
  desactivar(id: number): Observable<void> {
    this.logger.debug(TipoFinalidadService.name, `${this.desactivar.name}(`, '-', 'start');
    return this.http.patch<void>(`${this.endpointUrl}/${id}/desactivar`, undefined).pipe(
      tap(() => this.logger.debug(TipoFinalidadService.name, `${this.desactivar.name}()`, '-', 'end'))
    );
  }

  /**
   * Reactivar tipo fase
   * @param options opciones de búsqueda.
   */
  reactivar(id: number): Observable<void> {
    this.logger.debug(TipoFinalidadService.name, `${this.reactivar.name}(`, '-', 'start');
    return this.http.patch<void>(`${this.endpointUrl}/${id}/reactivar`, undefined).pipe(
      tap(() => this.logger.debug(TipoFinalidadService.name, `${this.reactivar.name}()`, '-', 'end'))
    );
  }

}
