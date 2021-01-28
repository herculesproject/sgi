import { Injectable } from '@angular/core';
import { ISocioPeriodoJustificacionDocumento } from '@core/models/csp/socio-periodo-justificacion-documento';
import { SgiRestService } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { HttpClient } from '@angular/common/http';
import { environment } from '@env';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class SocioPeriodoJustificacionDocumentoService extends SgiRestService<number, ISocioPeriodoJustificacionDocumento> {
  private static readonly MAPPING = '/socioperiodojustificaciondocumentos';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      SocioPeriodoJustificacionDocumentoService.name,
      logger,
      `${environment.serviceServers.csp}${SocioPeriodoJustificacionDocumentoService.MAPPING}`,
      http
    );
  }

  updateList(proyectoSocioPeriodoJustificacionId: number, entities: ISocioPeriodoJustificacionDocumento[]):
    Observable<ISocioPeriodoJustificacionDocumento[]> {
    this.logger.debug(SocioPeriodoJustificacionDocumentoService.name, `updateList()`,
      '-', 'start');
    return this.http.patch<ISocioPeriodoJustificacionDocumento[]>(
      `${this.endpointUrl}/${proyectoSocioPeriodoJustificacionId}`, entities).pipe(
        tap(() => this.logger.debug(SocioPeriodoJustificacionDocumentoService.name, `updateList()`, '-', 'end'))
      );
  }
}
