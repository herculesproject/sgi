import { Injectable } from '@angular/core';
import { SgiRestService } from '@sgi/framework/http';
import { HttpClient } from '@angular/common/http';
import { environment } from '@env';
import { NGXLogger } from 'ngx-logger';
import { IProyectoSocioPeriodoPago } from '@core/models/csp/proyecto-socio-periodo-pago';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ProyectoSocioPeriodoPagoService extends SgiRestService<number, IProyectoSocioPeriodoPago> {
  private static readonly MAPPING = '/proyectosocioperiodopagos';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      ProyectoSocioPeriodoPagoService.name,
      logger,
      `${environment.serviceServers.csp}${ProyectoSocioPeriodoPagoService.MAPPING}`,
      http
    );
  }

  updateList(proyectoSocioId: number, entities: IProyectoSocioPeriodoPago[]):
    Observable<IProyectoSocioPeriodoPago[]> {
    this.logger.debug(ProyectoSocioPeriodoPagoService.name, `updateList()`,
      '-', 'start');
    return this.http.patch<IProyectoSocioPeriodoPago[]>(
      `${this.endpointUrl}/${proyectoSocioId}`, entities).pipe(
        tap(() => this.logger.debug(ProyectoSocioPeriodoPagoService.name, `updateList()`, '-', 'end'))
      );
  }

}
