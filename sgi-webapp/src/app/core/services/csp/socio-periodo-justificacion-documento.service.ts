import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { SOCIO_PERIODO_JUSTIFICACION_DOCUMENTO_CONVERTER } from '@core/converters/csp/socio-periodo-justificacion-documento.converter';
import { ISocioPeriodoJustificacionDocumentoBackend } from '@core/models/csp/backend/socio-periodo-justificacion-documento-backend';
import { ISocioPeriodoJustificacionDocumento } from '@core/models/csp/socio-periodo-justificacion-documento';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class SocioPeriodoJustificacionDocumentoService
  extends SgiMutableRestService<number, ISocioPeriodoJustificacionDocumentoBackend, ISocioPeriodoJustificacionDocumento> {
  private static readonly MAPPING = '/socioperiodojustificaciondocumentos';

  constructor(protected http: HttpClient) {
    super(
      SocioPeriodoJustificacionDocumentoService.name,
      `${environment.serviceServers.csp}${SocioPeriodoJustificacionDocumentoService.MAPPING}`,
      http,
      SOCIO_PERIODO_JUSTIFICACION_DOCUMENTO_CONVERTER
    );
  }

  updateList(proyectoSocioPeriodoJustificacionId: number, entities: ISocioPeriodoJustificacionDocumento[]):
    Observable<ISocioPeriodoJustificacionDocumento[]> {
    return this.http.patch<ISocioPeriodoJustificacionDocumentoBackend[]>(
      `${this.endpointUrl}/${proyectoSocioPeriodoJustificacionId}`,
      this.converter.fromTargetArray(entities)
    ).pipe(
      map(response => this.converter.toTargetArray(response))
    );
  }
}
