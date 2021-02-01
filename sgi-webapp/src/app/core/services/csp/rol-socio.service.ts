import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IRolSocio } from '@core/models/csp/rol-socio';
import { environment } from '@env';
import { SgiRestService } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';

@Injectable({
  providedIn: 'root'
})
export class RolSocioService extends SgiRestService<number, IRolSocio>  {
  private static readonly MAPPING = '/rolsocios';

  constructor(protected readonly logger: NGXLogger, protected http: HttpClient) {
    super(
      RolSocioService.name,
      logger,
      `${environment.serviceServers.csp}${RolSocioService.MAPPING}`,
      http
    );
  }
}
