import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ITipoFase } from '@core/models/csp/tipos-configuracion';
import { environment } from '@env';
import { SgiRestService } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';

@Injectable({
  providedIn: 'root'
})
export class TipoFaseService extends SgiRestService<number, ITipoFase> {
  private static readonly MAPPING = '/tipofases';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      TipoFaseService.name,
      logger,
      `${environment.serviceServers.csp}${TipoFaseService.MAPPING}`,
      http
    );
  }

}
