import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IGrupo } from '@core/models/csp/grupo';
import { IGrupoEquipo } from '@core/models/csp/grupo-equipo';
import { environment } from '@env';
import { CreateCtor, FindAllCtor, FindByIdCtor, mixinCreate, mixinFindAll, mixinFindById, mixinUpdate, SgiRestBaseService, SgiRestFindOptions, SgiRestListResult, UpdateCtor } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IGrupoEquipoResponse } from '../grupo-equipo/grupo-equipo-response';
import { GRUPO_EQUIPO_RESPONSE_CONVERTER } from '../grupo-equipo/grupo-equipo-response.converter';
import { IGrupoRequest } from './grupo-request';
import { GRUPO_REQUEST_CONVERTER } from './grupo-request.converter';
import { IGrupoResponse } from './grupo-response';
import { GRUPO_RESPONSE_CONVERTER } from './grupo-response.converter';

// tslint:disable-next-line: variable-name
const _GrupoMixinBase:
  CreateCtor<IGrupo, IGrupo, IGrupoRequest, IGrupoResponse> &
  UpdateCtor<number, IGrupo, IGrupo, IGrupoRequest, IGrupoResponse> &
  FindByIdCtor<number, IGrupo, IGrupoResponse> &
  FindAllCtor<IGrupo, IGrupoResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinUpdate(
        mixinCreate(
          SgiRestBaseService,
          GRUPO_REQUEST_CONVERTER,
          GRUPO_RESPONSE_CONVERTER
        ),
        GRUPO_REQUEST_CONVERTER,
        GRUPO_RESPONSE_CONVERTER
      ),
      GRUPO_RESPONSE_CONVERTER
    ),
    GRUPO_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class GrupoService extends _GrupoMixinBase {
  private static readonly MAPPING = '/grupos';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${GrupoService.MAPPING}`,
      http,
    );
  }

  /**
   * Comprueba si existe el grupo
   *
   * @param id Identificador del grupo
   */
  exists(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  /**
   * Activa el grupo
   * 
   * @param id Identificador del grupo
   */
  activar(id: number): Observable<void> {
    const url = `${this.endpointUrl}/${id}/activar`;
    return this.http.patch<void>(url, { id });
  }

  /**
   * Desactiva el grupo
   * 
   * @param id Identificador del grupo
   */
  desactivar(id: number): Observable<void> {
    const url = `${this.endpointUrl}/${id}/desactivar`;
    return this.http.patch<void>(url, { id });
  }

  /**
   * Recupera la lista de miembros del equipo del grupo
   * 
   * @param id Identificador del grupo
   * @param options opciones de búsqueda.
   */
  findMiembrosEquipo(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IGrupoEquipo>> {
    return this.find<IGrupoEquipoResponse, IGrupoEquipo>(
      `${this.endpointUrl}/${id}/miembrosequipo`,
      options,
      GRUPO_EQUIPO_RESPONSE_CONVERTER
    );
  }

  /**
   * Recupera la lista de investigadores principales del grupo
   * 
   * @param id Identificador del grupo
   * @param options opciones de búsqueda.
   */
  findInvestigadoresPrincipales(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IGrupoEquipo>> {
    return this.find<IGrupoEquipoResponse, IGrupoEquipo>(
      `${this.endpointUrl}/${id}/investigadoresprincipales`,
      options,
      GRUPO_EQUIPO_RESPONSE_CONVERTER
    );
  }

}
