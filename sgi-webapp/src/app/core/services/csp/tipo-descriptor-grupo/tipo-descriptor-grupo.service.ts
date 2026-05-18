import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ITipoDescriptorGrupo } from '@core/models/csp/tipo-descriptor-grupo';
import { environment } from '@env';
import {
  CreateCtor,
  FindAllCtor,
  FindByIdCtor,
  mixinCreate,
  mixinFindAll,
  mixinFindById,
  mixinUpdate,
  RSQLSgiRestFilter,
  SgiRestBaseService,
  SgiRestFilterOperator,
  SgiRestFindOptions,
  SgiRestListResult,
  UpdateCtor
} from '@herculesproject/framework/http';
import { Observable } from 'rxjs';
import { ITipoDescriptorGrupoRequest } from './tipo-descriptor-grupo-request';
import { TIPO_DESCRIPTOR_GRUPO_REQUEST_CONVERTER } from './tipo-descriptor-grupo-request.converter';
import { ITipoDescriptorGrupoResponse } from './tipo-descriptor-grupo-response';
import { TIPO_DESCRIPTOR_GRUPO_RESPONSE_CONVERTER } from './tipo-descriptor-grupo-response.converter';

// tslint:disable-next-line: variable-name
const _TipoDescriptorGrupoServiceMixinBase:
  CreateCtor<ITipoDescriptorGrupo, ITipoDescriptorGrupo, ITipoDescriptorGrupoRequest, ITipoDescriptorGrupoResponse> &
  UpdateCtor<number, ITipoDescriptorGrupo, ITipoDescriptorGrupo, ITipoDescriptorGrupoRequest, ITipoDescriptorGrupoResponse> &
  FindByIdCtor<number, ITipoDescriptorGrupo, ITipoDescriptorGrupoResponse> &
  FindAllCtor<ITipoDescriptorGrupo, ITipoDescriptorGrupoResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinUpdate(
        mixinCreate(
          SgiRestBaseService,
          TIPO_DESCRIPTOR_GRUPO_REQUEST_CONVERTER,
          TIPO_DESCRIPTOR_GRUPO_RESPONSE_CONVERTER
        ),
        TIPO_DESCRIPTOR_GRUPO_REQUEST_CONVERTER,
        TIPO_DESCRIPTOR_GRUPO_RESPONSE_CONVERTER
      ),
      TIPO_DESCRIPTOR_GRUPO_RESPONSE_CONVERTER
    ),
    TIPO_DESCRIPTOR_GRUPO_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class TipoDescriptorGrupoService extends _TipoDescriptorGrupoServiceMixinBase {
  private static readonly MAPPING = '/tiposdescriptoresgrupo';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${TipoDescriptorGrupoService.MAPPING}`,
      http,
    );
  }

  findTodos(options?: SgiRestFindOptions): Observable<SgiRestListResult<ITipoDescriptorGrupo>> {
    return this.find<ITipoDescriptorGrupoResponse, ITipoDescriptorGrupo>(
      `${this.endpointUrl}/todos`,
      options,
      TIPO_DESCRIPTOR_GRUPO_RESPONSE_CONVERTER
    );
  }

  desactivar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/desactivar`, undefined);
  }

  reactivar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/reactivar`, undefined);
  }

  /**
   * Busca los tipos de descriptores de grupo cuyos ids estén en la lista indicada.
   *
   * @param ids lista de identificadores
   * @returns la lista de tipos de enlace
   */
  findAllByIdIn(ids: number[]): Observable<SgiRestListResult<ITipoDescriptorGrupo>> {
    const options: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter('id', SgiRestFilterOperator.IN, ids.map(id => id.toString()))
    };

    return this.findAll(options);
  }

}
