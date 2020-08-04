import { Injectable } from '@angular/core';
import { EstadoActa } from '@core/models/eti/estado-acta';
import { NGXLogger } from 'ngx-logger';
import { HttpClient } from '@angular/common/http';
import { environment } from '@env';
import { SgiRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root'
})
export class EstadoActaService extends SgiRestService<number, EstadoActa>{

  private static readonly MAPPING = '/estadoactas';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(EstadoActaService.name, logger,
      `${environment.serviceServers.eti}` + EstadoActaService.MAPPING, http);
  }
}
