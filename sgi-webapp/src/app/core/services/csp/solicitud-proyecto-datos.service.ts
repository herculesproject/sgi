import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ISolicitudProyectoDatos } from '@core/models/csp/solicitud-proyecto-datos';
import { environment } from '@env';
import { SgiRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root'
})
export class SolicitudProyectoDatosService extends SgiRestService<number, ISolicitudProyectoDatos> {
  private static readonly MAPPING = '/solicitudproyectodatos';

  constructor(protected http: HttpClient) {
    super(
      SolicitudProyectoDatosService.name,
      `${environment.serviceServers.csp}${SolicitudProyectoDatosService.MAPPING}`,
      http
    );
  }
}
