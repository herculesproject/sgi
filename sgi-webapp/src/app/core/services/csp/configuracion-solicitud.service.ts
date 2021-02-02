import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IConfiguracionSolicitud } from '@core/models/csp/configuracion-solicitud';
import { IDocumentoRequerido } from '@core/models/csp/documentos-requeridos-solicitud';
import { environment } from '@env';
import { SgiRestListResult, SgiRestService } from '@sgi/framework/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ConfiguracionSolicitudService extends SgiRestService<number, IConfiguracionSolicitud>{
  private static readonly MAPPING = '/convocatoria-configuracionsolicitudes';

  constructor(protected http: HttpClient) {
    super(
      ConfiguracionSolicitudService.name,
      `${environment.serviceServers.csp}${ConfiguracionSolicitudService.MAPPING}`,
      http
    );
  }

  /**
   * Recupera los documentos requeridos de solicitudes
   * @param id convocatoria
   */
  findAllConvocatoriaDocumentoRequeridoSolicitud(id: number): Observable<SgiRestListResult<IDocumentoRequerido>> {
    const endpointUrl = `${this.endpointUrl}/${id}/documentorequiridosolicitudes`;
    return this.find<IDocumentoRequerido, IDocumentoRequerido>(endpointUrl);
  }

}
