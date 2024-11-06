import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IDocumentoRequeridoSolicitud } from '@core/models/csp/documento-requerido-solicitud';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http';
import { IDocumentoRequeridoSolicitudResponse } from './documento-requerido-solicitud/documento-requerido-solicitud-response';
import { DOCUMENTO_REQUERIDO_SOLICITUD_CONVERTER } from './documento-requerido-solicitud/documento-requerido-solicitud.converter';

@Injectable({
  providedIn: 'root'
})
export class DocumentoRequeridoSolicitudService
  extends SgiMutableRestService<number, IDocumentoRequeridoSolicitudResponse, IDocumentoRequeridoSolicitud> {
  private static readonly MAPPING = '/documentorequiridosolicitudes';

  constructor(protected http: HttpClient) {
    super(
      DocumentoRequeridoSolicitudService.name,
      `${environment.serviceServers.csp}${DocumentoRequeridoSolicitudService.MAPPING}`,
      http,
      DOCUMENTO_REQUERIDO_SOLICITUD_CONVERTER
    );
  }
}
