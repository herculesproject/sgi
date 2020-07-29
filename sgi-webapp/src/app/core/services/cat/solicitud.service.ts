import { Injectable } from '@angular/core';
import { Registro } from '@core/models/cat/registro';
import { HttpClient } from '@angular/common/http';
import { environment } from '@env';
import { NGXLogger } from 'ngx-logger';
import { SgiRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root',
})
export class SolicitudService extends SgiRestService<Registro> {
  private static readonly MAPPING = '/registros';
  public registro: Registro;
  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      SolicitudService.name,
      logger,
      `${environment.serviceServers.cat}${SolicitudService.MAPPING}`,
      http
    );
  }
}
