import { Injectable } from '@angular/core';
import { Observable, BehaviorSubject } from 'rxjs';
import { Registro } from '@core/models/registro';
import { HttpClient } from '@angular/common/http';
import { BaseService } from './base.service';
import { environment } from '@env';
import { NGXLogger } from 'ngx-logger';

import { BaseRestService } from './base-rest.service';

@Injectable({
  providedIn: 'root',
})
export class SolicitudService extends BaseRestService<Registro> {
  public static REGISTRO_MAPPING = '/registros';
  public registro: Registro;
  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      SolicitudService.name,
      logger,
      `${environment.apiUrl}` + SolicitudService.REGISTRO_MAPPING,
      http
    );
  }
}
