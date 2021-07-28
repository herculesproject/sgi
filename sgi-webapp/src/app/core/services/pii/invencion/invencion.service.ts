import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IInvencion } from '@core/models/pii/invencion';
import { IInvencionAreaConocimiento } from '@core/models/pii/invencion-area-conocimiento';
import { IInvencionSectorAplicacion } from '@core/models/pii/invencion-sector-aplicacion';
import { IInvencionDocumento } from '@core/models/pii/invencion-documento';
import { environment } from '@env';
import {
  CreateCtor,
  FindAllCtor,
  FindByIdCtor,
  mixinCreate,
  mixinFindAll,
  mixinFindById, mixinUpdate, SgiRestBaseService, SgiRestFindOptions, SgiRestListResult, UpdateCtor
} from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IInvencionDocumentoResponse } from './invencion-documento/invencion-documento-response';
import { INVENCION_DOCUMENTO_RESPONSE_CONVERTER } from './invencion-documento/invencion-documento-response.converter';
import { INVENCION_AREACONOCIMIENTO_REQUEST_CONVERTER } from './invencion-area-conocimiento/invencion-area-conocimiento-request.converter';
import { IInvencionAreaConocimientoResponse } from './invencion-area-conocimiento/invencion-area-conocimiento-response';
import { INVENCION_AREACONOCIMIENTO_RESPONSE_CONVERTER } from './invencion-area-conocimiento/invencion-area-conocimiento-response.converter';
import { IInvencionRequest } from './invencion-request';
import { INVENCION_REQUEST_CONVERTER } from './invencion-request.converter';
import { IInvencionResponse } from './invencion-response';
import { INVENCION_RESPONSE_CONVERTER } from './invencion-response.converter';
import { INVENCION_SECTORAPLICACION_REQUEST_CONVERTER } from './invencion-sector-aplicacion/invencion-sector-aplicacion-request.converter';
import { IInvencionSectorAplicacionResponse } from './invencion-sector-aplicacion/invencion-sector-aplicacion-response';
import {
  INVENCION_SECTORAPLICACION_RESPONSE_CONVERTER
} from './invencion-sector-aplicacion/invencion-sector-aplicacion-response.converter';

// tslint:disable-next-line: variable-name
const _InvencionServiceMixinBase:
  FindAllCtor<IInvencion, IInvencionResponse> &
  FindByIdCtor<number, IInvencion, IInvencionResponse> &
  CreateCtor<IInvencion, IInvencion, IInvencionRequest, IInvencionResponse> &
  UpdateCtor<number, IInvencion, IInvencion, IInvencionRequest, IInvencionResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinCreate(
        mixinUpdate(
          SgiRestBaseService,
          INVENCION_REQUEST_CONVERTER,
          INVENCION_RESPONSE_CONVERTER
        ),
        INVENCION_REQUEST_CONVERTER,
        INVENCION_RESPONSE_CONVERTER
      ),
      INVENCION_RESPONSE_CONVERTER
    ),
    INVENCION_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class InvencionService extends _InvencionServiceMixinBase {

  private static readonly MAPPING = '/invenciones';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.pii}${InvencionService.MAPPING}`,
      http,
    );
  }

  /**
   * Muestra activos y no activos
   *
   * @param options opciones de búsqueda.
   */
  findTodos(options?: SgiRestFindOptions): Observable<SgiRestListResult<IInvencion>> {
    return this.find<IInvencionResponse, IInvencion>(
      `${this.endpointUrl}/todos`,
      options,
      INVENCION_RESPONSE_CONVERTER
    );
  }

  /**
   * Comprueba si existe una invencion
   *
   * @param id Id de la invencion
   */
  exists(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  /**
   * Recupera los sectores de aplicación asociados a la Invencion con el id indicado
   * @param id Identificador de la Invencion
   */
  findSectoresAplicacion(id: number): Observable<IInvencionSectorAplicacion[]> {
    const endpointUrl = `${this.endpointUrl}/${id}/sectoresaplicacion`;
    const params = new HttpParams().set('id', id.toString());
    return this.http.get<IInvencionSectorAplicacionResponse[]>(endpointUrl, { params })
      .pipe(
        map(response => {
          return INVENCION_SECTORAPLICACION_RESPONSE_CONVERTER.toTargetArray(response);
        })
      );
  }

  /**
   * Actualiza los sectores de aplicación asociados a la Invencion con el id indicado
   * @param id Identificador del Invencion
   * @param sectoresAplicacion sectores de aplicacion a actualizar
   */
  updateSectoresAplicacion(id: number, sectoresAplicacion: IInvencionSectorAplicacion[]): Observable<IInvencionSectorAplicacion[]> {
    return this.http.patch<IInvencionSectorAplicacionResponse[]>(`${this.endpointUrl}/${id}/sectoresaplicacion`,
      INVENCION_SECTORAPLICACION_REQUEST_CONVERTER.fromTargetArray(sectoresAplicacion)
    ).pipe(
      map((response => INVENCION_SECTORAPLICACION_RESPONSE_CONVERTER.toTargetArray(response)))
    );
  }

  /**
   * Obtiene todos los documentos de una invención dado el id
   * @param id
   * @param options
   * @returns documentos de una invencion
   */
  findAllInvencionDocumentos(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IInvencionDocumento>> {
    return this.find<IInvencionDocumentoResponse, IInvencionDocumento>(
      `${this.endpointUrl}/${id}/invenciondocumentos`,
      options,
      INVENCION_DOCUMENTO_RESPONSE_CONVERTER);
  }

  /**
   * Recupera las áreas de conocimiento asociadas a la Invencion con el id indicado
   * @param id Identificador de la Invencion
   */
  findAreasConocimiento(id: number): Observable<IInvencionAreaConocimiento[]> {
    const endpointUrl = `${this.endpointUrl}/${id}/areasconocimiento`;
    return this.http.get<IInvencionAreaConocimientoResponse[]>(endpointUrl)
      .pipe(
        map(response => {
          return INVENCION_AREACONOCIMIENTO_RESPONSE_CONVERTER.toTargetArray(response);
        })
      );
  }

  /**
   * Actualiza las áreas de conocimiento  asociadas a la Invencion con el id indicado
   * @param id Identificador del Invencion
   * @param areasConocimiento areas de conocimiento a actualizar
   */
  updateAreasConocimiento(id: number, areasConocimiento: IInvencionAreaConocimiento[]): Observable<IInvencionAreaConocimiento[]> {
    return this.http.patch<IInvencionAreaConocimientoResponse[]>(`${this.endpointUrl}/${id}/areasconocimiento`,
      INVENCION_AREACONOCIMIENTO_REQUEST_CONVERTER.fromTargetArray(areasConocimiento)
    ).pipe(
      map((response => INVENCION_AREACONOCIMIENTO_RESPONSE_CONVERTER.toTargetArray(response)))
    );
  }
}
