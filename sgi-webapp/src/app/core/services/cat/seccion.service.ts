import { Injectable } from '@angular/core';
import { NGXLogger } from 'ngx-logger';
import { HttpClient } from '@angular/common/http';

import { Seccion } from '@core/models/cat/seccion';

import { SgiRestService } from '@sgi/framework/http';
import { environment } from '@env';

@Injectable({
  providedIn: 'root',
})
export class SeccionService extends SgiRestService<Seccion> {
  private static readonly MAPPING = '/secciones';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      SeccionService.name,
      logger,
      `${environment.serviceServers.cat}${SeccionService.MAPPING}`,
      http
    );
  }

}
