import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { SOLICITUD_DOCUMENTO_RESPONSE_CONVERTER } from '@core/services/csp/solicitud-documento/solicitud-documento-response.converter';
import { ISolicitudDocumentoResponse } from '@core/services/csp/solicitud-documento/solicitud-documento-response';
import { ISolicitudDocumento } from '@core/models/csp/solicitud-documento';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root'
})
export class SolicitudDocumentoService extends SgiMutableRestService<number, ISolicitudDocumentoResponse, ISolicitudDocumento> {
  private static readonly MAPPING = '/solicituddocumentos';

  constructor(protected http: HttpClient) {
    super(
      SolicitudDocumentoService.name,
      `${environment.serviceServers.csp}${SolicitudDocumentoService.MAPPING}`,
      http,
      SOLICITUD_DOCUMENTO_RESPONSE_CONVERTER
    );
  }
}
