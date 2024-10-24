import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CONVOCATORIA_DOCUMENTO_CONVERTER } from '@core/services/csp/convocatoria-documento/convocatoria-documento.converter';
import { IConvocatoriaDocumentoResponse } from '@core/services/csp/convocatoria-documento/convocatoria-documento-response';
import { IConvocatoriaDocumento } from '@core/models/csp/convocatoria-documento';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root'
})
export class ConvocatoriaDocumentoService extends SgiMutableRestService<number, IConvocatoriaDocumentoResponse, IConvocatoriaDocumento> {
  private static readonly MAPPING = '/convocatoriadocumentos';

  constructor(protected http: HttpClient) {
    super(
      ConvocatoriaDocumentoService.name,
      `${environment.serviceServers.csp}${ConvocatoriaDocumentoService.MAPPING}`,
      http,
      CONVOCATORIA_DOCUMENTO_CONVERTER
    );
  }

}
