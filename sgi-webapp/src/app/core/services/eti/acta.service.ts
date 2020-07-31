import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Acta } from '@core/models/eti/acta';
import { environment } from '@env';
import { NGXLogger } from 'ngx-logger';
import { SgiRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root'
})
export class ActaService extends SgiRestService<number, Acta> {
  private static readonly MAPPING = '/actas';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      ActaService.name,
      logger,
      `${environment.serviceServers.eti}${ActaService.MAPPING}`,
      http
    );
  }
}
