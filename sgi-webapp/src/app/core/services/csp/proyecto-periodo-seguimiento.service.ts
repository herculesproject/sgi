import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IProyectoPeriodoSeguimiento } from '@core/models/csp/proyecto-periodo-seguimiento';
import { IProyectoPeriodoSeguimientoDocumento } from '@core/models/csp/proyecto-periodo-seguimiento-documento';
import { environment } from '@env';
import { SgiRestFindOptions, SgiRestListResult, SgiRestService } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ProyectoPeriodoSeguimientoService extends SgiRestService<number, IProyectoPeriodoSeguimiento>  {
  private static readonly MAPPING = '/proyectoperiodoseguimientos';


  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      ProyectoPeriodoSeguimientoService.name,
      logger,
      `${environment.serviceServers.csp}${ProyectoPeriodoSeguimientoService.MAPPING}`,
      http
    );
  }

  /**
   * Comprueba si existe un proyecto periodo seguimiento
   *
   * @param id Id del proyecto periodo seguimiento
   * @retrurn true/false
   */
  exists(id: number): Observable<boolean> {
    this.logger.debug(ProyectoPeriodoSeguimientoService.name, `exists(id: ${id})`, '-', 'start');
    const url = `${this.endpointUrl}/${id}`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(x => x.status === 200),
      tap(() => this.logger.debug(ProyectoPeriodoSeguimientoService.name, `exists(id: ${id})`, '-', 'end')),
    );
  }

  /**
   * Recupera los documentos del proyecto periodo de seguimiento
   * @param id Id del proyecto periodo de seguimiento
   * @return la lista de ProyectoPeridoSeguimientoDocumento
   */
  findDocumentos(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IProyectoPeriodoSeguimientoDocumento>> {
    this.logger.debug(ProyectoPeriodoSeguimientoService.name, `findDocumentos(id: ${id})`, '-', 'START');
    return this.find<IProyectoPeriodoSeguimientoDocumento, IProyectoPeriodoSeguimientoDocumento>(
      `${this.endpointUrl}/${id}/proyectoperiodoseguimientodocumentos`, options).pipe(
        tap(() => this.logger.debug(ProyectoPeriodoSeguimientoService.name, `findDocumentos(id: ${id})`, '-', 'END'))
      );
  }

  /**
 * Comprueba si existe documentos asociados al proyecto periodo seguimiento
 *
 * @param id Id del proyecto periodo seguimiento
 * @retrurn true/false
 */
  existsDocumentos(id: number): Observable<boolean> {
    this.logger.debug(ProyectoPeriodoSeguimientoService.name, `existsDocumentos(id: ${id})`, '-', 'start');
    const url = `${this.endpointUrl}/${id}/proyectoperiodoseguimientodocumentos`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200),
      tap(() => this.logger.debug(ProyectoPeriodoSeguimientoService.name, `existsDocumentos(id: ${id})`, '-', 'end')),
    );
  }

}
