import { Injectable } from '@angular/core';
import { SgiRestService } from '@sgi/framework/http';
import { ISolicitudHito } from '@core/models/csp/solicitud-hito';
import { NGXLogger } from 'ngx-logger';
import { HttpClient } from '@angular/common/http';
import { environment } from '@env';

@Injectable({
  providedIn: 'root'
})
export class SolicitudHitoService extends SgiRestService<number, ISolicitudHito> {
  static readonly MAPPING = '/solicitudhitos';

  constructor(protected readonly logger: NGXLogger, protected http: HttpClient) {
    super(
      SolicitudHitoService.name,
      logger,
      `${environment.serviceServers.csp}${SolicitudHitoService.MAPPING}`,
      http
    );
  }
}
