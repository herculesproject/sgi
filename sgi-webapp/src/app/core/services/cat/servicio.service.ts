import { Injectable } from '@angular/core';
import { Servicio } from '@core/models/cat/servicio';
import { NGXLogger } from 'ngx-logger';
import { HttpClient } from '@angular/common/http';
import { environment } from '@env';
import { SgiRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root',
})
export class ServicioService extends SgiRestService<Servicio> {
  private static readonly MAPPING = '/servicios';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      ServicioService.name,
      logger,
      `${environment.serviceServers.cat}${ServicioService.MAPPING}`,
      http
    );
  }
}
