import { Injectable } from '@angular/core';
import { Servicio } from '@core/models/cat/servicio';
import { NGXLogger } from 'ngx-logger';
import { HttpClient } from '@angular/common/http';
import { BaseRestService } from '../base-rest.service';
import { environment } from '@env';

@Injectable({
  providedIn: 'root',
})
export class ServicioService extends BaseRestService<Servicio> {
  public static SERVICIO_MAPPING = '/servicios';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(ServicioService.name, logger,
      `${environment.apiUrl}` + ServicioService.SERVICIO_MAPPING, http);
  }
}
