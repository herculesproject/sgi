import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { DOCUMENTO_REQUERIDO_CONVERTER } from '@core/converters/csp/documento-requerido.converter';
import { IDocumentoRequeridoBackend } from '@core/models/csp/backend/documento-requerido-backend';
import { IDocumentoRequerido } from '@core/models/csp/documentos-requeridos-solicitud';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root'
})
export class DocumentoRequeridoService extends SgiMutableRestService<number, IDocumentoRequeridoBackend, IDocumentoRequerido> {
  private static readonly MAPPING = '/documentorequiridosolicitudes';

  constructor(protected http: HttpClient) {
    super(
      DocumentoRequeridoService.name,
      `${environment.serviceServers.csp}${DocumentoRequeridoService.MAPPING}`,
      http,
      DOCUMENTO_REQUERIDO_CONVERTER
    );
  }
}
