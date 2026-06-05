import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ISolicitudProyecto } from '@core/models/csp/solicitud-proyecto';
import { ISolicitudProyectoUnidadVinculacion } from '@core/models/csp/solicitud-proyecto-unidad-vinculacion';
import { environment } from '@env';
import {
  CreateCtor,
  FindAllCtor,
  FindByIdCtor,
  mixinCreate,
  mixinFindAll,
  mixinFindById,
  mixinUpdate,
  SgiRestBaseService,
  SgiRestFindOptions,
  SgiRestListResult,
  UpdateCtor
} from '@herculesproject/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { SOLICITUD_PROYECTO_UNIDAD_VINCULACION_REQUEST_CONVERTER } from './solicitud-proyecto-unidad-vinculacion/solicitud-proyecto-unidad-vinculacion-request.converter';
import { ISolicitudProyectoUnidadVinculacionResponse } from './solicitud-proyecto-unidad-vinculacion/solicitud-proyecto-unidad-vinculacion-response';
import { SOLICITUD_PROYECTO_UNIDAD_VINCULACION_RESPONSE_CONVERTER } from './solicitud-proyecto-unidad-vinculacion/solicitud-proyecto-unidad-vinculacion-response.converter';
import { ISolicitudProyectoResponse } from './solicitud-proyecto/solicitud-proyecto-response';
import { SOLICITUD_PROYECTO_RESPONSE_CONVERTER } from './solicitud-proyecto/solicitud-proyecto-response.converter';

// tslint:disable-next-line: variable-name
const _SolicitudProyectoServiceMixinBase:
  CreateCtor<ISolicitudProyecto, ISolicitudProyecto, ISolicitudProyectoResponse, ISolicitudProyectoResponse> &
  UpdateCtor<number, ISolicitudProyecto, ISolicitudProyecto, ISolicitudProyectoResponse, ISolicitudProyectoResponse> &
  FindAllCtor<ISolicitudProyecto, ISolicitudProyectoResponse> &
  FindByIdCtor<number, ISolicitudProyecto, ISolicitudProyectoResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinUpdate(
        mixinCreate(
          SgiRestBaseService,
          SOLICITUD_PROYECTO_RESPONSE_CONVERTER,
          SOLICITUD_PROYECTO_RESPONSE_CONVERTER
        ),
        SOLICITUD_PROYECTO_RESPONSE_CONVERTER,
        SOLICITUD_PROYECTO_RESPONSE_CONVERTER
      ),
      SOLICITUD_PROYECTO_RESPONSE_CONVERTER),
    SOLICITUD_PROYECTO_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class SolicitudProyectoService extends _SolicitudProyectoServiceMixinBase {

  private static readonly MAPPING = '/solicitudproyecto';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${SolicitudProyectoService.MAPPING}`,
      http
    );
  }

  /**
   * Comprueba si SolicitudProyectoDatos tiene SolicitudProyectoPresupuesto
   * relacionado
   *
   * @param id solicitudProyectoDatos
   */
  hasSolicitudPresupuesto(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}/solicitudpresupuesto`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  /**
   * Comprueba si SolicitudProyectoDatos tiene SolicitudProyectoSocio
   * relacionado
   *
   * @param id solicitudProyectoDatos
   */
  hasSolicitudSocio(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}/solicitudsocio`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }


  hasPeriodosJustificacion(solicitudProyectoId: number): Observable<boolean> {

    const url = `${this.endpointUrl}/${solicitudProyectoId}/solicitudproyectosocios/periodosjustificacion`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  hasPeriodosPago(solicitudProyectoId: number): Observable<boolean> {

    const url = `${this.endpointUrl}/${solicitudProyectoId}/solicitudproyectosocios/periodospago`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  hasAnySolicitudProyectoSocioWithRolCoordinador(solicitudProyectoId: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${solicitudProyectoId}/solicitudproyectosocios/coordinador`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  /**
   * Devuelve las unidades de vinculación asociadas a la solicitud de proyecto con el id indicado.
   *
   * @param id identificador de la solicitud de proyecto
   * @param options opciones de búsqueda
   * @returns la lista de unidades de vinculación
   */
  findUnidadesVinculacion(id: number, options?: SgiRestFindOptions):
    Observable<SgiRestListResult<ISolicitudProyectoUnidadVinculacion>> {
    return this.find<ISolicitudProyectoUnidadVinculacionResponse, ISolicitudProyectoUnidadVinculacion>(
      `${this.endpointUrl}/${id}/unidades-vinculacion`,
      options,
      SOLICITUD_PROYECTO_UNIDAD_VINCULACION_RESPONSE_CONVERTER
    );
  }

  /**
   * Actualiza las unidades de vinculación de la solicitud de proyecto con el id indicado.
   *
   * @param id identificador de la solicitud de proyecto
   * @param unidadesVinculacion lista completa de unidades a establecer
   * @returns la lista de unidades actualizada
   */
  updateUnidadesVinculacion(id: number, unidadesVinculacion: ISolicitudProyectoUnidadVinculacion[]):
    Observable<ISolicitudProyectoUnidadVinculacion[]> {
    return this.http.patch<ISolicitudProyectoUnidadVinculacionResponse[]>(
      `${this.endpointUrl}/${id}/unidades-vinculacion`,
      SOLICITUD_PROYECTO_UNIDAD_VINCULACION_REQUEST_CONVERTER.fromTargetArray(unidadesVinculacion)
    ).pipe(
      map(response => SOLICITUD_PROYECTO_UNIDAD_VINCULACION_RESPONSE_CONVERTER.toTargetArray(response))
    );
  }

}
