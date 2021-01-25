import { Injectable } from '@angular/core';
import { ISolicitudProyectoPeriodoJustificacion } from '@core/models/csp/solicitud-proyecto-periodo-justificacion';
import { SgiRestService } from '@sgi/framework/http';
import { environment } from '@env';
import { NGXLogger } from 'ngx-logger';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class SolicitudProyectoPeriodoJustificacionService extends SgiRestService<number, ISolicitudProyectoPeriodoJustificacion> {
  private static readonly MAPPING = '/solicitudproyectoperiodojustificaciones';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      SolicitudProyectoPeriodoJustificacionService.name,
      logger,
      `${environment.serviceServers.csp}${SolicitudProyectoPeriodoJustificacionService.MAPPING}`,
      http
    );
  }

  updateList(proyectoSolictudSocioId: number, entities: ISolicitudProyectoPeriodoJustificacion[]):
    Observable<ISolicitudProyectoPeriodoJustificacion[]> {
    this.logger.debug(SolicitudProyectoPeriodoJustificacionService.name, `updateList()`,
      '-', 'start');
    return this.http.patch<ISolicitudProyectoPeriodoJustificacion[]>(
      `${this.endpointUrl}/${proyectoSolictudSocioId}`, entities).pipe(
        tap(() => this.logger.debug(SolicitudProyectoPeriodoJustificacionService.name, `updateList()`, '-', 'end'))
      );
  }
}
