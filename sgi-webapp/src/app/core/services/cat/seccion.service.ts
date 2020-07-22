import { Injectable } from '@angular/core';
import { NGXLogger } from 'ngx-logger';
import { HttpClient } from '@angular/common/http';

import { Seccion } from '@core/models/cat/seccion';

import { BaseRestService } from '../base-rest.service';
import { environment } from '@env';

@Injectable({
  providedIn: 'root',
})
export class SeccionService extends BaseRestService<Seccion> {
  public static SECCION_MAPPING = '/secciones';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(SeccionService.name, logger,
      `${environment.apiUrl}` + SeccionService.SECCION_MAPPING, http);
  }

}
