import { Injectable } from '@angular/core';
import { NGXLogger } from 'ngx-logger';
import { HttpClient } from '@angular/common/http';
import { SgiReadOnlyRestService } from '@sgi/framework/http';
import { environment } from '@env';
import { IComite } from '@core/models/eti/comite';

@Injectable({
  providedIn: 'root',
})
export class ComiteService extends SgiReadOnlyRestService<number, IComite> {
  private static readonly MAPPING = '/comites';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      ComiteService.name,
      logger,
      `${environment.serviceServers.eti}${ComiteService.MAPPING}`,
      http
    );
  }
}
