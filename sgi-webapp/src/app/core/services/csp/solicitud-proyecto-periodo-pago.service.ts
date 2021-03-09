import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { SOLICITUD_PROYECTO_PERIODO_PAGO_CONVERTER } from '@core/converters/csp/solicitud-proyecto-periodo-pago.converter';
import { ISolicitudProyectoPeriodoPagoBackend } from '@core/models/csp/backend/solicitud-proyecto-periodo-pago-backend';
import { ISolicitudProyectoPeriodoPago } from '@core/models/csp/solicitud-proyecto-periodo-pago';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class SolicitudProyectoPeriodoPagoService
  extends SgiMutableRestService<number, ISolicitudProyectoPeriodoPagoBackend, ISolicitudProyectoPeriodoPago> {
  private static readonly MAPPING = '/solicitudproyectoperiodopago';

  constructor(protected http: HttpClient) {
    super(
      SolicitudProyectoPeriodoPagoService.name,
      `${environment.serviceServers.csp}${SolicitudProyectoPeriodoPagoService.MAPPING}`,
      http,
      SOLICITUD_PROYECTO_PERIODO_PAGO_CONVERTER
    );
  }

  updateList(proyectoSolictudSocioId: number, entities: ISolicitudProyectoPeriodoPago[]):
    Observable<ISolicitudProyectoPeriodoPago[]> {
    return this.http.patch<ISolicitudProyectoPeriodoPagoBackend[]>(
      `${this.endpointUrl}/${proyectoSolictudSocioId}`,
      this.converter.fromTargetArray(entities)
    ).pipe(
      map(response => this.converter.toTargetArray(response))
    );
  }
}
