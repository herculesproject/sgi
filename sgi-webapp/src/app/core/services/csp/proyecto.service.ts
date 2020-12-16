import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IProyecto } from '@core/models/csp/proyecto';
import { environment } from '@env';
import { SgiRestFindOptions, SgiRestListResult, SgiRestService } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ProyectoService extends SgiRestService<number, IProyecto> {
  private static readonly MAPPING = '/proyectos';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      ProyectoService.name,
      logger,
      `${environment.serviceServers.csp}${ProyectoService.MAPPING}`,
      http
    );
  }

  /**
   * Devuelve todos que estén asociadas a
   * las unidades de gestión a las que esté vinculado el usuario
   * @param options opciones de búsqueda.
   */
  findTodos(options?: SgiRestFindOptions): Observable<SgiRestListResult<IProyecto>> {
    this.logger.debug(ProyectoService.name, `${this.findTodos.name}(`, '-', 'START');
    return this.find<IProyecto, IProyecto>(`${this.endpointUrl}/todos`, options).pipe(
      tap(() => this.logger.debug(ProyectoService.name, `${this.findTodos.name}()`, '-', 'END'))
    );
  }

  /**
   * Desactivar proyecto
   * @param options opciones de búsqueda.
   */
  desactivar(id: number): Observable<void> {
    this.logger.debug(ProyectoService.name, `${this.desactivar.name}(`, '-', 'start');
    return this.http.patch<void>(`${this.endpointUrl}/${id}/desactivar`, undefined).pipe(
      tap(() => this.logger.debug(ProyectoService.name, `${this.desactivar.name}()`, '-', 'end'))
    );
  }

  /**
   * Reactivar proyecto
   * @param options opciones de búsqueda.
   */
  reactivar(id: number): Observable<void> {
    this.logger.debug(ProyectoService.name, `${this.reactivar.name}(`, '-', 'start');
    return this.http.patch<void>(`${this.endpointUrl}/${id}/reactivar`, undefined).pipe(
      tap(() => this.logger.debug(ProyectoService.name, `${this.reactivar.name}()`, '-', 'end'))
    );
  }
}
