import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IConfiguracionSolicitud } from '@core/models/csp/configuracion-solicitud';
import { IDocumentoRequerido } from '@core/models/csp/documentos-requeridos-solicitud';
import { environment } from '@env';
import { SgiRestListResult, SgiRestService } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ConfiguracionSolicitudService extends SgiRestService<number, IConfiguracionSolicitud>{
  private static readonly MAPPING = '/convocatoria-configuracionsolicitudes';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      ConfiguracionSolicitudService.name,
      logger,
      `${environment.serviceServers.csp}${ConfiguracionSolicitudService.MAPPING}`,
      http
    );
  }

  /**
   * Recupera los documentos requeridos de solicitudes
   * @param id convocatoria
   */
  findAllConvocatoriaDocumentoRequeridoSolicitud(id: number): Observable<SgiRestListResult<IDocumentoRequerido>> {
    this.logger.debug(ConfiguracionSolicitudService.name, `findAllConvocatoriaDocumentoRequeridoSolicitud(${id})`, '-', 'start');
    const endpointUrl = `${this.endpointUrl}/${id}/documentorequiridosolicitudes`;
    return this.find<IDocumentoRequerido, IDocumentoRequerido>(endpointUrl)
      .pipe(
        tap(() =>
          this.logger.debug(ConfiguracionSolicitudService.name, `findAllConvocatoriaDocumentoRequeridoSolicitud(${id})`, '-', 'end'))
      );
  }

}
