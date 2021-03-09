import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CONFIGURACION_SOLICITUD_CONVERTER } from '@core/converters/csp/configuracion-solicitud.converter';
import { DOCUMENTO_REQUERIDO_CONVERTER } from '@core/converters/csp/documento-requerido.converter';
import { IConfiguracionSolicitudBackend } from '@core/models/csp/backend/configuracion-solicitud-backend';
import { IDocumentoRequeridoBackend } from '@core/models/csp/backend/documento-requerido-backend';
import { IConfiguracionSolicitud } from '@core/models/csp/configuracion-solicitud';
import { IDocumentoRequerido } from '@core/models/csp/documentos-requeridos-solicitud';
import { ITipoDocumento } from '@core/models/csp/tipos-configuracion';
import { environment } from '@env';
import { SgiMutableRestService, SgiRestListResult } from '@sgi/framework/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ConfiguracionSolicitudService extends SgiMutableRestService<number, IConfiguracionSolicitudBackend, IConfiguracionSolicitud>{
  private static readonly MAPPING = '/convocatoria-configuracionsolicitudes';

  constructor(protected http: HttpClient) {
    super(
      ConfiguracionSolicitudService.name,
      `${environment.serviceServers.csp}${ConfiguracionSolicitudService.MAPPING}`,
      http,
      CONFIGURACION_SOLICITUD_CONVERTER
    );
  }

  /**
   * Recupera los documentos requeridos de solicitudes
   * @param id convocatoria
   */
  findAllConvocatoriaDocumentoRequeridoSolicitud(id: number): Observable<SgiRestListResult<IDocumentoRequerido>> {
    const endpointUrl = `${this.endpointUrl}/${id}/documentorequiridosolicitudes`;
    return this.find<IDocumentoRequeridoBackend, IDocumentoRequerido>(endpointUrl, undefined, DOCUMENTO_REQUERIDO_CONVERTER);
  }

  /**
   * Recupera tipos de documento asociados a la fase de presentación de solicitudes de la convocatoria.
   * 
   * @param id Id de la convocatoria
   */
  findAllTipoDocumentosFasePresentacion(id: number):
    Observable<SgiRestListResult<ITipoDocumento>> {
    const endpointUrl = `${this.endpointUrl}/${id}/tipodocumentofasepresentaciones`;
    return this.find<ITipoDocumento, ITipoDocumento>(endpointUrl);
  }

}
