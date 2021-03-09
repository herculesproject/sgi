import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { PROYECTO_SOCIO_PERIODO_JUSTIFICACION_CONVERTER } from '@core/converters/csp/proyecto-socio-periodo-justificacion.converter';
import { SOCIO_PERIODO_JUSTIFICACION_DOCUMENTO_CONVERTER } from '@core/converters/csp/socio-periodo-justificacion-documento.converter';
import { IProyectoSocioPeriodoJustificacionBackend } from '@core/models/csp/backend/proyecto-socio-periodo-justificacion-backend';
import { ISocioPeriodoJustificacionDocumentoBackend } from '@core/models/csp/backend/socio-periodo-justificacion-documento-backend';
import { IProyectoSocioPeriodoJustificacion } from '@core/models/csp/proyecto-socio-periodo-justificacion';
import { ISocioPeriodoJustificacionDocumento } from '@core/models/csp/socio-periodo-justificacion-documento';
import { environment } from '@env';
import { SgiMutableRestService, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ProyectoSocioPeriodoJustificacionService
  extends SgiMutableRestService<number, IProyectoSocioPeriodoJustificacionBackend, IProyectoSocioPeriodoJustificacion> {
  private static readonly MAPPING = '/proyectosocioperiodojustificaciones';

  constructor(protected http: HttpClient) {
    super(
      ProyectoSocioPeriodoJustificacionService.name,
      `${environment.serviceServers.csp}${ProyectoSocioPeriodoJustificacionService.MAPPING}`,
      http,
      PROYECTO_SOCIO_PERIODO_JUSTIFICACION_CONVERTER
    );
  }

  updateList(proyectoSolictudSocioId: number, entities: IProyectoSocioPeriodoJustificacion[]):
    Observable<IProyectoSocioPeriodoJustificacion[]> {
    return this.http.patch<IProyectoSocioPeriodoJustificacionBackend[]>(
      `${this.endpointUrl}/${proyectoSolictudSocioId}`,
      this.converter.fromTargetArray(entities)
    ).pipe(
      map(response => this.converter.toTargetArray(response))
    );
  }

  /**
   * Devuelve el listado de ISocioPeriodoJustificacionDocumento de un IProyectoSocioPeriodoJustificacion
   *
   * @param id Id del IProyectoSocioPeriodoJustificacion
   */
  findAllSocioPeriodoJustificacionDocumento(id: number, options?: SgiRestFindOptions)
    : Observable<SgiRestListResult<ISocioPeriodoJustificacionDocumento>> {
    return this.find<ISocioPeriodoJustificacionDocumentoBackend, ISocioPeriodoJustificacionDocumento>(
      `${this.endpointUrl}/${id}/socioperiodojustificaciondocumentos`,
      options,
      SOCIO_PERIODO_JUSTIFICACION_DOCUMENTO_CONVERTER
    );
  }
}
