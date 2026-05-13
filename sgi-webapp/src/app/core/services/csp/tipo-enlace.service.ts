import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ITipoEnlace } from '@core/models/csp/tipos-configuracion';
import { environment } from '@env';
import {
  CreateCtor,
  FindAllCtor,
  mixinCreate,
  mixinFindAll,
  mixinUpdate,
  RSQLSgiRestFilter,
  SgiRestBaseService,
  SgiRestFilterOperator,
  SgiRestFindOptions,
  SgiRestListResult,
  UpdateCtor
} from '@herculesproject/framework/http';
import { Observable } from 'rxjs';
import { ITipoEnlaceResponse } from './tipo-enlace/tipo-enlace-response';
import { TIPO_ENLACE_RESPONSE_CONVERTER } from './tipo-enlace/tipo-enlace-response.converter';

// tslint:disable-next-line: variable-name
const _TipoEnlaceServiceMixinBase:
  CreateCtor<ITipoEnlace, ITipoEnlace, ITipoEnlaceResponse, ITipoEnlaceResponse> &
  UpdateCtor<number, ITipoEnlace, ITipoEnlace, ITipoEnlaceResponse, ITipoEnlaceResponse> &
  FindAllCtor<ITipoEnlace, ITipoEnlaceResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinUpdate(
      mixinCreate(
        SgiRestBaseService,
        TIPO_ENLACE_RESPONSE_CONVERTER,
        TIPO_ENLACE_RESPONSE_CONVERTER
      ),
      TIPO_ENLACE_RESPONSE_CONVERTER,
      TIPO_ENLACE_RESPONSE_CONVERTER
    ),
    TIPO_ENLACE_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class TipoEnlaceService extends _TipoEnlaceServiceMixinBase {
  private static readonly MAPPING = '/tipoenlaces';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${TipoEnlaceService.MAPPING}`,
      http
    );
  }

  /**
   * Muestra activos y no activos
   * @param options opciones de búsqueda.
   */
  findTodos(options?: SgiRestFindOptions): Observable<SgiRestListResult<ITipoEnlace>> {
    return this.find<ITipoEnlaceResponse, ITipoEnlace>(`${this.endpointUrl}/todos`, options, TIPO_ENLACE_RESPONSE_CONVERTER);
  }

  /**
   * Desactivar tipo enlace
   * @param options opciones de búsqueda.
   */
  desactivar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/desactivar`, undefined);
  }

  /**
   * Reactivar tipo enlace
   * @param options opciones de búsqueda.
   */
  reactivar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/reactivar`, undefined);
  }

  /**
   * Busca los tipos de enlace cuyos ids estén en la lista indicada.
   *
   * @param ids lista de identificadores
   * @returns la lista de tipos de enlace
   */
  findAllByIdIn(ids: number[]): Observable<SgiRestListResult<ITipoEnlace>> {
    const options: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter('id', SgiRestFilterOperator.IN, ids.map(id => id.toString()))
    };

    return this.findAll(options);
  }

}
