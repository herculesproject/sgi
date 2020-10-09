import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ITipoHito } from '@core/models/csp/tipos-configuracion';
import { environment } from '@env';
import { SgiRestService } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';

@Injectable({
  providedIn: 'root'
})
export class TipoHitoService extends SgiRestService<number, ITipoHito> {
  private static readonly MAPPING = '/tipohitos';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      TipoHitoService.name,
      logger,
      `${environment.serviceServers.csp}${TipoHitoService.MAPPING}`,
      http
    );
  }
}

