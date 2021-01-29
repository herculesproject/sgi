import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IProyectoProrroga } from '@core/models/csp/proyecto-prorroga';
import { IProyectoProrrogaDocumento } from '@core/models/csp/proyecto-prorroga-documento';
import { environment } from '@env';
import { SgiRestFindOptions, SgiRestListResult, SgiRestService } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ProyectoProrrogaService extends SgiRestService<number, IProyectoProrroga>  {
  private static readonly MAPPING = '/proyectoprorrogas';


  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      ProyectoProrrogaService.name,
      logger,
      `${environment.serviceServers.csp}${ProyectoProrrogaService.MAPPING}`,
      http
    );
  }

  /**
   * Comprueba si existe un proyecto prorroga
   *
   * @param id Id del proyecto prorroga
   * @retrurn true/false
   */
  exists(id: number): Observable<boolean> {
    this.logger.debug(ProyectoProrrogaService.name, `exists(id: ${id})`, '-', 'start');
    const url = `${this.endpointUrl}/${id}`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(x => x.status === 200),
      tap(() => this.logger.debug(ProyectoProrrogaService.name, `exists(id: ${id})`, '-', 'end')),
    );
  }

  /**
   * Recupera los documentos del proyecto periodo de seguimiento
   * @param id Id del proyecto periodo de seguimiento
   * @return la lista de ProyectoPeridoSeguimientoDocumento
   */
  findDocumentos(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IProyectoProrrogaDocumento>> {
    this.logger.debug(ProyectoProrrogaService.name, `findDocumentos(id: ${id})`, '-', 'START');
    return this.find<IProyectoProrrogaDocumento, IProyectoProrrogaDocumento>(
      `${this.endpointUrl}/${id}/prorrogadocumentos`, options).pipe(
        tap(() => this.logger.debug(ProyectoProrrogaService.name, `findDocumentos(id: ${id})`, '-', 'END'))
      );
  }

  /**
 * Comprueba si existe documentos asociados al proyecto prorroga
 *
 * @param id Id del proyecto prorroga
 * @retrurn true/false
 */
  existsDocumentos(id: number): Observable<boolean> {
    this.logger.debug(ProyectoProrrogaService.name, `existsDocumentos(id: ${id})`, '-', 'start');
    const url = `${this.endpointUrl}/${id}/prorrogadocumentos`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200),
      tap(() => this.logger.debug(ProyectoProrrogaService.name, `existsDocumentos(id: ${id})`, '-', 'end')),
    );
  }

}
