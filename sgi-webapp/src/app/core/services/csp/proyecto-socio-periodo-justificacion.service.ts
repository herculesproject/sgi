import { Injectable } from '@angular/core';
import { IProyectoSocioPeriodoJustificacion } from '@core/models/csp/proyecto-socio-periodo-justificacion';
import { SgiRestService, SgiRestListResult, SgiRestFindOptions } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { HttpClient } from '@angular/common/http';
import { environment } from '@env';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { ISocioPeriodoJustificacionDocumento } from '@core/models/csp/socio-periodo-justificacion-documento';

@Injectable({
  providedIn: 'root'
})
export class ProyectoSocioPeriodoJustificacionService extends SgiRestService<number, IProyectoSocioPeriodoJustificacion> {
  private static readonly MAPPING = '/proyectosocioperiodojustificaciones';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      ProyectoSocioPeriodoJustificacionService.name,
      logger,
      `${environment.serviceServers.csp}${ProyectoSocioPeriodoJustificacionService.MAPPING}`,
      http
    );
  }

  updateList(proyectoSolictudSocioId: number, entities: IProyectoSocioPeriodoJustificacion[]):
    Observable<IProyectoSocioPeriodoJustificacion[]> {
    this.logger.debug(ProyectoSocioPeriodoJustificacionService.name, `updateList()`,
      '-', 'start');
    return this.http.patch<IProyectoSocioPeriodoJustificacion[]>(
      `${this.endpointUrl}/${proyectoSolictudSocioId}`, entities).pipe(
        tap(() => this.logger.debug(ProyectoSocioPeriodoJustificacionService.name, `updateList()`, '-', 'end'))
      );
  }

  /**
   * Devuelve el listado de ISocioPeriodoJustificacionDocumento de un IProyectoSocioPeriodoJustificacion
   *
   * @param id Id del IProyectoSocioPeriodoJustificacion
   */
  findAllSocioPeriodoJustificacionDocumento(id: number, options?: SgiRestFindOptions)
    : Observable<SgiRestListResult<ISocioPeriodoJustificacionDocumento>> {
    this.logger.debug(ProyectoSocioPeriodoJustificacionService.name,
      `findAllSocioPeriodoJustificacionDocumento(id: ${id})`, '-', 'start');
    return this.find<ISocioPeriodoJustificacionDocumento, ISocioPeriodoJustificacionDocumento>(
      `${this.endpointUrl}/${id}/socioperiodojustificaciondocumentos`, options).pipe(
        tap(() => this.logger.debug(ProyectoSocioPeriodoJustificacionService.name,
          `findAllSocioPeriodoJustificacionDocumento(id: ${id})`, '-', 'end'))
      );
  }
}
