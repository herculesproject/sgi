import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ITipoFinalidad } from '@core/models/csp/tipo-finalidad';
import { environment } from '@env';
import { SgiRestService } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';

@Injectable({
  providedIn: 'root'
})
export class TipoFinalidadService extends SgiRestService<number, ITipoFinalidad> {
  private static readonly MAPPING = '/tipofinalidades';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      TipoFinalidadService.name,
      logger,
      `${environment.serviceServers.csp}${TipoFinalidadService.MAPPING}`,
      http
    );
  }
}
