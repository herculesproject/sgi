import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ISolicitudDocumento } from '@core/models/csp/solicitud-documento';
import { environment } from '@env';
import { SgiRestService } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';

@Injectable({
  providedIn: 'root'
})
export class SolicitudDocumentoService extends SgiRestService<number, ISolicitudDocumento> {
  private static readonly MAPPING = '/solicituddocumentos';

  constructor(protected readonly logger: NGXLogger, protected http: HttpClient) {
    super(
      SolicitudDocumentoService.name,
      logger,
      `${environment.serviceServers.csp}${SolicitudDocumentoService.MAPPING}`,
      http
    );
  }
}
