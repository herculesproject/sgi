import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IIncidenciaDocumentacionRequerimiento } from '@core/models/csp/incidencia-documentacion-requerimiento';
import { IRequerimientoJustificacion } from '@core/models/csp/requerimiento-justificacion';
import { environment } from '@env';
import {
  CreateCtor, FindByIdCtor, mixinCreate, mixinFindById,
  mixinUpdate, SgiRestBaseService, SgiRestFindOptions,
  SgiRestListResult, UpdateCtor
} from '@sgi/framework/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { IIncidenciaDocumentacionRequerimientoResponse } from '../incidencia-documentacion-requerimiento/incidencia-documentacion-requerimiento-response';
import { INCIDENCIA_DOCUMENTACION_REQUERIMIENTO_RESPONSE_CONVERTER } from '../incidencia-documentacion-requerimiento/incidencia-documentacion-requerimiento-response.converter';
import { IRequerimientoJustificacionRequest } from './requerimiento-justificacion-request';
import { REQUERIMIENTO_JUSTIFICACION_REQUEST_CONVERTER } from './requerimiento-justificacion-request.converter';
import { IRequerimientoJustificacionResponse } from './requerimiento-justificacion-response';
import { REQUERIMIENTO_JUSTIFICACION_RESPONSE_CONVERTER } from './requerimiento-justificacion-response.converter';

// tslint:disable-next-line: variable-name
const _RequerimientoJustificacionMixinBase:
  FindByIdCtor<number, IRequerimientoJustificacion, IRequerimientoJustificacionResponse> &
  CreateCtor<IRequerimientoJustificacion, IRequerimientoJustificacion,
    IRequerimientoJustificacionRequest, IRequerimientoJustificacionResponse> &
  UpdateCtor<number, IRequerimientoJustificacion, IRequerimientoJustificacion,
    IRequerimientoJustificacionRequest, IRequerimientoJustificacionResponse> &
  typeof SgiRestBaseService = mixinFindById(
    mixinCreate(
      mixinUpdate(
        SgiRestBaseService,
        REQUERIMIENTO_JUSTIFICACION_REQUEST_CONVERTER,
        REQUERIMIENTO_JUSTIFICACION_RESPONSE_CONVERTER
      ),
      REQUERIMIENTO_JUSTIFICACION_REQUEST_CONVERTER,
      REQUERIMIENTO_JUSTIFICACION_RESPONSE_CONVERTER
    ),
    REQUERIMIENTO_JUSTIFICACION_RESPONSE_CONVERTER);

@Injectable({
  providedIn: 'root'
})
export class RequerimientoJustificacionService extends _RequerimientoJustificacionMixinBase {

  private static readonly MAPPING = '/requerimientos-justificacion';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${RequerimientoJustificacionService.MAPPING}`,
      http
    );
  }

  /**
   * Elimina un Requerimiento Justificacion por id.
   *
   * @param id Id del Requerimiento Justificacion
   */
  deleteById(id: number) {
    return this.http.delete<void>(`${this.endpointUrl}/${id}`).pipe(
      catchError((error: HttpErrorResponse) => {
        return throwError(error);
      })
    );
  }

  findIncidenciasDocumentacion(id: number, options?: SgiRestFindOptions):
    Observable<SgiRestListResult<IIncidenciaDocumentacionRequerimiento>> {
    return this.find<IIncidenciaDocumentacionRequerimientoResponse, IIncidenciaDocumentacionRequerimiento>(
      `${this.endpointUrl}/${id}/incidencias-documentacion`,
      options,
      INCIDENCIA_DOCUMENTACION_REQUERIMIENTO_RESPONSE_CONVERTER);
  }
}
