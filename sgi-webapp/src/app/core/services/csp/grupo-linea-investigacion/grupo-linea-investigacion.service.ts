import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IGrupoLineaInvestigacion } from '@core/models/csp/grupo-linea-investigacion';
import { IGrupoLineaInvestigador } from '@core/models/csp/grupo-linea-investigador';
import { environment } from '@env';
import { CreateCtor, FindByIdCtor, mixinCreate, mixinFindById, mixinUpdate, SgiRestBaseService, SgiRestFindOptions, SgiRestListResult, UpdateCtor } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IGrupoLineaInvestigadorResponse } from '../grupo-linea-investigador/grupo-linea-investigador-response';
import { GRUPO_LINEA_INVESTIGADOR_RESPONSE_CONVERTER } from '../grupo-linea-investigador/grupo-linea-investigador-response.converter';
import { IGrupoLineaInvestigacionRequest } from './grupo-linea-investigacion-request';
import { GRUPO_LINEA_INVESTIGACION_REQUEST_CONVERTER } from './grupo-linea-investigacion-request.converter';
import { IGrupoLineaInvestigacionResponse } from './grupo-linea-investigacion-response';
import { GRUPO_LINEA_INVESTIGACION_RESPONSE_CONVERTER } from './grupo-linea-investigacion-response.converter';

// tslint:disable-next-line: variable-name
const _GrupoLineaInvestigacionMixinBase:
  CreateCtor<IGrupoLineaInvestigacion, IGrupoLineaInvestigacion, IGrupoLineaInvestigacionRequest, IGrupoLineaInvestigacionResponse> &
  UpdateCtor<number, IGrupoLineaInvestigacion, IGrupoLineaInvestigacion, IGrupoLineaInvestigacionRequest, IGrupoLineaInvestigacionResponse> &
  FindByIdCtor<number, IGrupoLineaInvestigacion, IGrupoLineaInvestigacionResponse> &
  typeof SgiRestBaseService = mixinFindById(
    mixinUpdate(
      mixinCreate(
        SgiRestBaseService,
        GRUPO_LINEA_INVESTIGACION_REQUEST_CONVERTER,
        GRUPO_LINEA_INVESTIGACION_RESPONSE_CONVERTER
      ),
      GRUPO_LINEA_INVESTIGACION_REQUEST_CONVERTER,
      GRUPO_LINEA_INVESTIGACION_RESPONSE_CONVERTER
    ),
    GRUPO_LINEA_INVESTIGACION_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class GrupoLineaInvestigacionService extends _GrupoLineaInvestigacionMixinBase {
  private static readonly MAPPING = '/gruposlineasinvestigacion';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${GrupoLineaInvestigacionService.MAPPING}`,
      http,
    );
  }

  public deleteById(id: number): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/${id}`);
  }

  /**
   * Actualiza el listado de GrupoLineaInvestigacion asociados a un grupo
   *
   * @param id Id del IGrupoLineaInvestigacion
   * @param entities Listado de IGrupoLineaInvestigacion
   */
  updateList(id: number, entities: IGrupoLineaInvestigacion[]): Observable<IGrupoLineaInvestigacion[]> {
    return this.http.patch<IGrupoLineaInvestigacionResponse[]>(
      `${this.endpointUrl}/${id}`,
      GRUPO_LINEA_INVESTIGACION_REQUEST_CONVERTER.fromTargetArray(entities)
    ).pipe(
      map(response => GRUPO_LINEA_INVESTIGACION_RESPONSE_CONVERTER.toTargetArray(response))
    );
  }

  /**
   * Comprueba si existe el grupo lineas de investigación
   *
   * @param id Identificador del grupo linea de investigación
   */
  exists(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  /**
   * Comprueba si tiene permisos de edición del grupo líneas de investigación
   *
   * @param id Id del grupo linea de investigación
   */
  modificable(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}/modificable`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  /**
   * Recupera la lista de líneas de investigadores
   * @param id Identificador del grupo de investigación
   * @param options opciones de búsqueda.
   */
  findLineasInvestigadores(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IGrupoLineaInvestigador>> {
    return this.find<IGrupoLineaInvestigadorResponse, IGrupoLineaInvestigador>(
      `${this.endpointUrl}/${id}/lineas-investigadores`,
      options,
      GRUPO_LINEA_INVESTIGADOR_RESPONSE_CONVERTER
    );
  }

}
