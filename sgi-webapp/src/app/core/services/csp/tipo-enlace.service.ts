import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ITipoEnlace } from '@core/models/csp/tipo-enlace';
import { environment } from '@env';
import { SgiRestService } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';

@Injectable({
  providedIn: 'root'
})
export class TipoEnlaceService extends SgiRestService<number, ITipoEnlace> {
  private static readonly MAPPING = '/tipoenlaces';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      TipoEnlaceService.name,
      logger,
      `${environment.serviceServers.csp}${TipoEnlaceService.MAPPING}`,
      http
    );
  }

}
