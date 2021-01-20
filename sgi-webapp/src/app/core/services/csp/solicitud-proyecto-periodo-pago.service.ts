import { Injectable } from '@angular/core';
import { ISolicitudProyectoPeriodoPago } from '@core/models/csp/solicitud-proyecto-periodo-pago';
import { SgiRestService } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { HttpClient } from '@angular/common/http';
import { environment } from '@env';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class SolicitudProyectoPeriodoPagoService extends SgiRestService<number, ISolicitudProyectoPeriodoPago> {
  private static readonly MAPPING = '/solicitudproyectoperiodopago';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      SolicitudProyectoPeriodoPagoService.name,
      logger,
      `${environment.serviceServers.csp}${SolicitudProyectoPeriodoPagoService.MAPPING}`,
      http
    );
  }

  updateList(proyectoSolictudSocioId: number, entities: ISolicitudProyectoPeriodoPago[]):
    Observable<ISolicitudProyectoPeriodoPago[]> {
    this.logger.debug(SolicitudProyectoPeriodoPagoService.name, `updateList()`,
      '-', 'start');
    return this.http.patch<ISolicitudProyectoPeriodoPago[]>(
      `${this.endpointUrl}/${proyectoSolictudSocioId}`, entities).pipe(
        tap(() => this.logger.debug(SolicitudProyectoPeriodoPagoService.name, `updateList()`, '-', 'end'))
      );
  }
}
