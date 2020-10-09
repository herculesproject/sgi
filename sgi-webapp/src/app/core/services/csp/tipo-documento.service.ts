import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ITipoDocumento } from '@core/models/csp/tipos-configuracion';
import { environment } from '@env';
import { SgiRestService } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';

@Injectable({
  providedIn: 'root'
})
export class TipoDocumentoService extends SgiRestService<number, ITipoDocumento> {
  private static readonly MAPPING = '/tipodocumentos';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      TipoDocumentoService.name,
      logger,
      `${environment.serviceServers.csp}${TipoDocumentoService.MAPPING}`,
      http
    );
  }
}
